package dev.maiky.minetopia.modules.scripts;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.scripts.commands.ScriptsCommand;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import me.lucko.helper.Schedulers;
import me.lucko.helper.internal.LoaderUtils;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.menu.scheme.SchemeMapping;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.text3.Text;
import me.lucko.scriptcontroller.ScriptController;
import me.lucko.scriptcontroller.bindings.BindingsBuilder;
import me.lucko.scriptcontroller.bindings.BindingsSupplier;
import me.lucko.scriptcontroller.environment.ScriptEnvironment;
import me.lucko.scriptcontroller.environment.loader.ScriptLoadingExecutor;
import me.lucko.scriptcontroller.environment.script.Script;
import me.lucko.scriptcontroller.environment.settings.EnvironmentSettings;
import me.lucko.scriptcontroller.logging.SystemLogger;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class ScriptsModule implements MinetopiaModule {

    private boolean enabled;

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void reload() {
        this.disable();
        this.enable();
    }

    private static final String[] DEFAULT_IMPORT_INCLUDES = new String[]{
            // include all of the packages in helper
            "me.lucko.helper",
            "com.flowpowered.math",
            "net.jodah.expiringmap",
            // include all of the packages in bukkit
            "org.bukkit",
            "com.destroystokyo.paper",
            "org.spigotmc.event",
            "dev.maiky.minetopia"
    };

    private static final String[] DEFAULT_IMPORT_EXCLUDES = new String[]{
            // exclude craftbukkit classes
            "org.bukkit.craftbukkit",
            // exclude helper-js dependencies (the classpath scanner itself)
            "me.lucko.helper.js.external"
    };

    private ScriptController controller;
    private ScriptEnvironment environment;

    @Override
    public void enable() {
        this.enabled = true;

        ClassGraph classGraph = new ClassGraph()
                .whitelistPackages(DEFAULT_IMPORT_INCLUDES)
                .blacklistPackages(DEFAULT_IMPORT_EXCLUDES)
                .addClassLoader(Bukkit.getServer().getClass().getClassLoader());

        // add the classloaders for helper implementation plugins
        LoaderUtils.getHelperImplementationPlugins().forEach(pl -> classGraph.addClassLoader(pl.getClass().getClassLoader()));

        Set<String> defaultPackages = classGraph.scan()
                .getAllClasses()
                .stream()
                .map(ClassInfo::getPackageName)
                .collect(Collectors.toSet());

        // setup the script controller
        Minetopia.getInstance().getLogger().info("Registreren van script manager...");
        this.controller = ScriptController.builder()
                .logger(SystemLogger.usingJavaLogger(Minetopia.getInstance().getLogger()))
                .defaultEnvironmentSettings(EnvironmentSettings.builder()
                        .loadExecutor(new HelperLoadingExecutor())
                        .runExecutor(Schedulers.sync())
                        .pollRate(Ticks.to(20L, TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                        .initScript("init.js")
                        .withBindings(new GeneralScriptBindings())
                        .withBindings(new HelperScriptBindings(Minetopia.getInstance()))
                        .withDefaultPackageImports(defaultPackages)
                        .build()
                )
                .build();

        // get script directory
        Path scriptDirectory = Paths.get("plugins/Minetopia/scripts");
        if (!Files.isDirectory(scriptDirectory)) {
            Minetopia.getInstance().saveResource("scripts/init.js", false);
            Minetopia.getInstance().saveResource("scripts/commands/discord.js", false);
            Minetopia.getInstance().saveResource("scripts/commands/watismijnlevel.js", false);
        }

        // init a new environment for our scripts
        Minetopia.getInstance().getLogger().info("Bezig met het opzetten van een scriptomgeving in de map " + scriptDirectory + " (" + scriptDirectory.toAbsolutePath() + ")");
        this.environment = this.controller.setupNewEnvironment(scriptDirectory);

        Minetopia.getInstance().getLogger().info("Succesvol opgezet!");

        Minetopia.getInstance().getCommandManager().registerCommand(new ScriptsCommand(this));
    }

    public ScriptController getController() {
        return this.controller;
    }

    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public String getName() {
        return "Scripts";
    }

    private static final class HelperLoadingExecutor implements ScriptLoadingExecutor {
        @Override
        public AutoCloseable scheduleAtFixedRate(Runnable runnable, long l, TimeUnit timeUnit) {
            return Schedulers.builder()
                    .async()
                    .every(l, timeUnit)
                    .run(runnable);
        }

        @Override
        public void execute(@Nonnull Runnable command) {
            Schedulers.async().run(command);
        }
    }

    /**
     * Some misc functions to help with using Java collections in JS
     */
    private static final class GeneralScriptBindings implements BindingsSupplier {
        private static final Supplier<ArrayList> ARRAY_LIST = ArrayList::new;
        private static final Supplier<LinkedList> LINKED_LIST = LinkedList::new;
        private static final Supplier<HashSet> HASH_SET = HashSet::new;
        private static final Supplier<HashMap> HASH_MAP = HashMap::new;
        private static final Supplier<CopyOnWriteArrayList> COPY_ON_WRITE_ARRAY_LIST = CopyOnWriteArrayList::new;
        private static final Supplier<Set> CONCURRENT_HASH_SET = ConcurrentHashMap::newKeySet;
        private static final Supplier<ConcurrentHashMap> CONCURRENT_HASH_MAP = ConcurrentHashMap::new;
        private static final Function<Object[], ArrayList> LIST_OF = objects -> new ArrayList<>(Arrays.asList(objects));
        private static final Function<Object[], HashSet> SET_OF = objects -> new HashSet<>(Arrays.asList(objects));
        private static final Function<Object[], ImmutableList> IMMUTABLE_LIST_OF = ImmutableList::copyOf;
        private static final Function<Object[], ImmutableSet> IMMUTABLE_SET_OF = ImmutableSet::copyOf;
        private static final Function<String, UUID> PARSE_UUID = s -> {
            try {
                return UUID.fromString(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };

        @Override
        public void supplyBindings(Script script, BindingsBuilder bindings) {
            bindings.put("newArrayList", ARRAY_LIST);
            bindings.put("newLinkedList", LINKED_LIST);
            bindings.put("newHashSet", HASH_SET);
            bindings.put("newHashMap", HASH_MAP);
            bindings.put("newCopyOnWriteArrayList", COPY_ON_WRITE_ARRAY_LIST);
            bindings.put("newConcurrentHashSet", CONCURRENT_HASH_SET);
            bindings.put("newConcurrentHashMap", CONCURRENT_HASH_MAP);
            bindings.put("listOf", LIST_OF);
            bindings.put("setOf", SET_OF);
            bindings.put("immutableListOf", IMMUTABLE_LIST_OF);
            bindings.put("immutableSetOf", IMMUTABLE_SET_OF);
            bindings.put("parseUuid", PARSE_UUID);
        }
    }

    /**
     * Script bindings for helper utilities
     */
    private static final class HelperScriptBindings implements BindingsSupplier {

        private final Minetopia plugin;

        private HelperScriptBindings(Minetopia plugin) {
            this.plugin = plugin;
        }

        @Override
        public void supplyBindings(Script script, BindingsBuilder bindings) {
            // provide a terminable registry
            CompositeTerminable registry = CompositeTerminable.create();
            script.getClosables().bind(registry);
            bindings.put("registry", registry);

            // provide core server classes
            bindings.put("server", Bukkit.getServer());
            bindings.put("plugin", this.plugin);
            bindings.put("services", Bukkit.getServicesManager());

            // some util functions
            bindings.put("colorize", (Function<Object, String>) HelperScriptBindings::colorize);
            bindings.put("newMetadataKey", (Function<Object, MetadataKey>) HelperScriptBindings::newMetadataKey);
            bindings.put("newEmptyScheme", (Supplier<MenuScheme>) HelperScriptBindings::newScheme);
            bindings.put("newScheme", (Function<SchemeMapping, MenuScheme>) HelperScriptBindings::newScheme);
        }

        private static String colorize(Object object) {
            return Text.colorize(object.toString());
        }

        private static <T> MetadataKey<T> newMetadataKey(Object id) {
            return MetadataKey.create(id.toString(), new TypeToken<T>(){});
        }

        private static MenuScheme newScheme() {
            return new MenuScheme();
        }

        private static MenuScheme newScheme(SchemeMapping mapping) {
            return new MenuScheme(mapping);
        }
    }

}
