/*
 * This file is part of Minetopia.
 *
 *  Copyright (c) Maiky1304 (Maiky) <maiky@blackmt.nl>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package dev.maiky.minetopia.util;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bank.bank.Permission;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.*;

public enum Message {

	COMMON_COMMAND_UNKNOWNSUBCOMMAND("&cUnknown subcommand"),
	COMMON_COMMAND_SYNTAX("&cGebruik: /{0} {1} {2}", String.class, String.class, String.class),
	COMMON_COMMAND_HELP_HEADER("&6/{0} <subcommand> <arg>...", String.class),
	COMMON_COMMAND_HELP_EXAMPLE("&a/{0} &2{1} &a{2}&f- &a{3}", String.class, String.class, String.class, String.class),

	COMMON_ERROR_SELF_NOINVSPACE("&cJe hebt geen genoeg inventory ruimte."),
	COMMON_ERROR_OTHER_NOINVSPACE("&cDeze speler heeft geen genoeg inventory ruimte."),
	COMMON_ERROR_PLAYEROFFLINE("&cDeze speler is niet online!"),
	COMMON_ERROR_NOTANUMBER("&cDit is geen getal!"),
	COMMON_ERROR_NOTENOUGHMONEY("&cJe hebt geen genoeg geld voor deze transactie."),
	COMMON_ERROR_USEATLEAST("&cGebruik een waarde van minimaal {0}.", Integer.class),
	COMMON_ERROR_INVALIDTIMEFORMAT("&cDit is geen geldig tijdformaat, gebruik bijvoorbeeld: 1, 1min, 1mo of 1y"),
	COMMON_ERROR_INVALIDCOLORCODE("&cDit is geen geldige kleurencode."),
	COMMON_ERROR_NOTLOOKINGATBLOCK("&cJe kijkt niet naar een blok!"),
	COMMON_ERROR_INVALIDBLOCKTYPE("&cDit is geen geldig block soort!"),

	COMMON_GUI_PAGEFORWARD("Volgende pagina"),
	COMMON_GUI_PAGEBACKWARDS("Vorige pagina"),
	COMMON_GUI_CLOSEMENU("{0}Sluit menu", ChatColor.class),

	BANKING_ERROR_NOPRIVATE("&cJe kan hier geen persoonlijke rekening voor gebruiken."),
	BANKING_ERROR_NOBANKACCOUNT("&cEr is geen bankrekening gevonden in de categorie {0} met het ID {1}.", String.class, Integer.class),
	BANKING_ERROR_NOREGISTRATION("&cGeen registratie gevonden!"),
	BANKING_ERROR_ALREADYHASPERMISSION("&cDe speler {0} heeft al de permission override {1} op deze rekening.", String.class),
	BANKING_ERROR_ALREADYHASALLPERMISSIONS("&cDe speler {0} heeft al al de permission overrides op deze rekening.", String.class),
	BANKING_ERROR_DOESNTHAVEPERMISSION("&cDe speler heeft de permission override {0} niet op deze rekening.", String.class),
	BANKING_ERROR_PINCONSOLE_LOOKING("&cJe kijkt niet naar een pin console blok!"),
	BANKING_ERROR_PINCONSOLE_FINISHPINPAYMENT("&cMaak eerst de huidige betaling af voordat je een nieuwe start!"),
	BANKING_ERROR_PINCONSOLE_ATLEAST1EUROCENT("&cDe betaling moet minimaal 1 euro cent zijn."),

	BANKING_PINCONSOLE_DELETED("&6Je hebt succesvol de pinconsole verwijderd waar je naar keek."),
	BANKING_PINCONSOLE_SUCCESS("&6Je hebt succesvol deze pinconsole ingesteld op de bankrekening ID &c{0} &6in de categorie &c{1}&6.", Integer.class, String.class),
	BANKING_ACCOUNT_DELETED("&6Je hebt succesvol een bankrekening verwijderd die het ID &c{0} &6had in de categorie &c{1}&6.", Integer.class, String.class),
	BANKING_ACCOUNT_SUCCESS("&6Je hebt succesvol een bankrekening aangemaakt met het ID &c{0} &6in de categorie &c{1}&6.", Integer.class, String.class),
	BANKING_ACCOUNT_RENAMED("&6Je hebt succesvol de naam van deze bankrekening gewijzigd naar &c{0}&6."),
	BANKING_OVERRIDES_DELETED("&6Je hebbt succesvol de permission override &c{0} &6gerevoked voor de speler &c{1} &6op de bankrekening met het ID &c{2} &6in de " +
			"categorie &c{3}&6.", String.class, String.class, Integer.class, String.class),
	BANKING_OVERRIDES_SUCCESS("&6Je hebt succesvol de permission override &c{0} &6aangemaakt voor de speler &c{1} &6op de bankrekening met het ID &c{2} &6in de " +
			"categorie &c{3}&6.", String.class, String.class, Integer.class, String.class),
	BANKING_LIST_HEADER("&6------ &cPagina %s/%s &6------", Integer.class, Integer.class),
	BANKING_LIST_NOACCOUNTS("&cEr zijn geen rekeningen in deze categorie."),
	BANKING_LIST_PAGENOTFOUND("&cEr bestaat geen pagina met dit nummer."),

	BANKING_REQUESTS_CREATED("&6Je hebt een verzoek aangemaakt klik nu op een &cpinconsole &6om het verzoek te versturen."),
	BANKING_REQUESTS_STARTED("&6Je hebt het betaalproces met &c{0} &6gestart, met een bedrag van &c{1}&6.", String.class, String.class),
	BANKING_REQUESTS_RECEIVED("&6Je hebt een pinverzoek ontvangen van &c{0} &6met een bedrag van &c{1}&6.", String.class, String.class),
	BANKING_REQUESTS_ACCEPTKEYWORD("accept"),
	BANKING_REQUESTS_TYPEACCEPT("&6Typ &c{0} &6in de chat om de betaling te voltooien!", String.class),
	BANKING_REQUESTS_SUCCESS_WORKER("&6De betaling met &c{0} &6van &c{1} &6is voltooid.", String.class, String.class),
	BANKING_REQUESTS_SUCCESS_CLIENT("&6Je hebt de betaling met &c{0} &6van &c{1} &6voltooid.", String.class, String.class),
	BANKING_REQUESTS_INSUFFICIENTBALANCE_WORKER("&cDe betaling is mislukt omdat de klant onvoldoende banksaldo heeft."),
	BANKING_REQUESTS_INSUFFICIENTBALANCE_CLIENT("&cJe hebt onvoldoende saldo het betaalproces is geannuleerd."),
	BANKING_REQUESTS_CLIENTLEFT("&6De klant heeft de server &cverlaten &6tijdens het betaalproces daarom is de betaling geannuleerd."),
	BANKING_REQUESTS_WORKERLEFT("&6De winkelier heeft de server &cverlaten &6tijdens het betaalproces daarom is de betaling geannuleerd."),
	BANKING_REQUESTS_CANCELLED_WORKER("&6De klant &c{0} &6heeft het pinverzoek geweigerd.", String.class),
	BANKING_REQUESTS_CANCELLED_CLIENT("&6Je hebt het pinverzoek succesvol geannuleerd."),

	BANKING_GUI_CHOOSEACCOUNT_TITLE("&3Kies een rekening:"),
	BANKING_GUI_CHOOSEACOUNT_DEFAULTLORE("&5(Privé rekening)"),
	BANKING_GUI_BALANCE_TITLE("&3Saldo: &b{0}", String.class),
	BANKING_GUI_BALANCE_ERROR_MISSINGPERMISSIONS("&cJe mist het &4{0} &cvoor deze rekening, vraag toestemming aan de rekeninghouder.", String.class),
	BANKING_GUI_BALANCE_SUCCESS_DEPOSIT("&6Je hebt &c%s &6gestort op je %s rekening.", String.class, String.class),
	BANKING_GUI_BALANCE_SUCCESS_WITHDRAW("&6Je hebt &c%s &6opgenomen van je %s rekening.", String.class, String.class),
	BANKING_GUI_BALANCE_QUESTION_AMOUNT("&7Voer een hoeveelheid &ebriefjes &7in die je wilt opnemen:"),
	BANKING_GUI_BALANCE_COUNTERFEIT("&cHet geld wat je probeert te storten wordt niet geaccepteerd door de pinautomaat."),
	BANKING_GUI_CHOOSETYPE_TITLE("&3Kies een rekeningsoort:"),
	BANKING_GUI_CHOOSETYPE_ERROR_NOACCOUNT("&cJij hebt geen rekening in deze categorie."),
	BANKING_GUI_CHOOSETYPE_ERROR_NOACCOUNTOTHER("&4{0} &cheeft geen rekening in deze categorie.", String.class),
	BANKING_GUI_CHOOSETYPE_ERROR_CANTOPENOTHERPERSONAL("&cJe kunt de persoonlijke rekening van &4{0} &cniet openen omdat hij/zij niet online is.", String.class),

	BANKING_DEBITCARD_USE("&6Banksaldo: &c{0}", String.class),

	BAGS_ERROR_OPEN("&cEr is iets fout gegaan met het ophalen van jouw bag, contacteer een developer."),
	BAGS_ERROR_NOTINHAND("&cJe hebt geen koffer in je hand!"),
	BAGS_ERROR_NOTBYID("&cEr is geen koffer met het ID {0}.", Integer.class),
	BAGS_HISTORY_HEADER("&3Geschiedenis voor de koffer &b{0}&3:", Integer.class),
	BAGS_HISTORY_LINE(" &3- &b{0} &3op &b{1}", String.class, String.class),
	BAGS_HISTORY_NODATA(" &3- &cGeen data gevonden..."),
	BAGS_HISTORY_TOOK("&3Het ophalen duurde &b{0}ms&3.", Integer.class),
	BAGS_CREATED("&6Je hebt succesvol een koffer gemaakt met het ID &c{0}&6, deze is toegevoegd aan je inventory.", Integer.class),
	BAGS_OPEN("&6Je hebt je koffer geopend."),

	BOOSTERS_ADD("&6Succesvol de booster soort &c{0} &6toegevoegd aan &c{1}&6 van &c{2}%&6.", String.class, String.class, Integer.class),
	BOOSTERS_REMOVE("&6Succesvol de booster soort &c{0} &6verwijderd van &c{1}&6 van &c{2}%&6."),
	BOOSTERS_INFO("&6De speler &c{0} &6heeft in totaal &c{1}% &6{2}boost.", String.class, Integer.class, String.class),
	BOOSTERS_ACTIVATED_SELF("&6Je hebt een {0}booster geactiveerd van &c{1}%&6.", String.class, Integer.class),
	BOOSTERS_ACTIVATED_BROADCAST("&6Er is een {0}booster geactiveerd door &c{1} &6van" +
			" &c{2}%&6.", String.class, String.class, Integer.class),
	BOOSTERS_ERROR_NOTENOUGHGRAYSHARD("&cJe hebt geen genoeg Grayshardboost hiervoor."),
	BOOSTERS_ERROR_NOTENOUGHGOLDSHARD("&cJe hebt geen genoeg Goldshardboost hiervoor."),
	BOOSTERS_BOSSBAR_GRAYSHARD_TITLE("GrayShard Booster: {0}% <= {1}", Integer.class, String.class),
	BOOSTERS_BOSSBAR_GRAYSHARD_BARCOLOR("BLUE"),
	BOOSTERS_BOSSBAR_GRAYSHARD_BARSTYLE("SOLID"),
	BOOSTERS_BOSSBAR_GOLDSHARD_TITLE("GoldShard Booster: {0}% <= {1}", Integer.class, String.class),
	BOOSTERS_BOSSBAR_GOLDSHARD_BARCOLOR("YELLOW"),
	BOOSTERS_BOSSBAR_GOLDSHARD_BARSTYLE("SOLID"),

	COLORS_ERROR_ALREADYOWNED("&cDeze speler heeft deze kleur al in zijn/haar bezit!"),
	COLORS_ERROR_NOTOWNED("&cDeze speler heeft deze kleur niet in zijn/haar bezit!"),
	COLORS_ADD_TEMPORARY("&6Je hebt succesvol de kleur &c{0} &6toegevoegd aan de speler &c{1}&6 voor &c{2}&6.", String.class, String.class, String.class),
	COLORS_ADD_PERMANENT("&6Je hebt succesvol de kleur &c{0} &6toegevoegd aan de speler &c{1}&6.", String.class, String.class),
	COLORS_REMOVE("&6Je hebt succesvol de kleur &c{0} &6verwijderd van de speler &c{1}&6.", String.class, String.class),
	COLORS_LIST_HEADERFOOTER("&6&m----------------------------------------------------"),
	COLORS_LIST_ENTRY(" &c- &{0}{1} &c{2}", String.class, String.class, String.class),
	COLORS_GUI_CHATCOLOR_TITLE("&0Kies een chatkleur"),
	COLORS_GUI_LEVELCOLOR_TITLE("&0Kies een levelkleur"),
	COLORS_GUI_PREFIXCOLOR_TITLE("&0Kies een prefixkleur"),
	COLORS_GUI_UNLOCKEDLORE_PERMANENT(Collections.singletonList("&a[Unlocked]")),
	COLORS_GUI_UNLOCKEDLORE_TEMPORARY(Arrays.asList("&a[Unlocked]", "Verloopt op {0}"), String.class),
	COLORS_GUI_LOCKEDLORE(Collections.singletonList("&c[Locked]")),
	COLORS_GUI_COLORCHANGED("&6Je hebt de kleur van je {0} veranderd naar &c{1}&6.", String.class, String.class),

	DDGITEMS_SELECTGUI_TITLE("&3DDG Items"),
	DDGITEMS_CATEGORY_TITLE("&3DDG Items &7- &0Pagina {0}", Integer.class),

	DISTRICTS_CREATED("&6Je hebt succesvol een district aangemaakt met het blok &c{0} &6verander de naam doormiddel van &c{1}", String.class, String.class),
	DISTRICTS_DELETED("&6Je hebt succesvol het district met het blok &c{0} &6verwijderd.", String.class),
	DISTRICTS_RENAMED("&6Je hebt succesvol de naam van het district met het blok &c{0} &6veranderd naar &c{1}&6.", String.class, String.class),
	DISTRICTS_RECOLORED("&6Je hebt succesvol de kleur van het district met het blok &c{0} &6veranderd naar &c{1}&6.", String.class, String.class),
	DISTRICTS_LIST_ENTRY("§2- §a{0} §2[§a{1}§2]", String.class, String.class),
	DISTRICTS_LIST_EMPTY("&cEr zijn nog geen districten aangemaakt, doe dit via het &4{1} &ccommand.", String.class),
	DISTRICTS_ERROR_DOESNTEXIST("&cEr bestaat geen district met het blok {0}.", String.class),
	DISTRICTS_ERROR_ALREADYEXISTS("&cEr bestaat al een district met het blok {0}, gebruik §4/{1} {2} §com deze te verwijderen.", String.class, String.class, String.class),

	GUNS_CREATED("&6Je hebt succesvol een wapen gemaakt met het modelnaam &c{0}&6.", String.class),
	GUNS_GETAMMO("&6Je hebt succesvol ammo gemaakt voor een wapen met de modelnaam &c{0}&6.", String.class),
	GUNS_SETDURABILITY("&6Je hebt succesvol de durability van de gun met de license &c{0} &6veranderd naar &c{1}&6.", String.class, Integer.class),
	GUNS_HIT_TOVICTIM("§6Je bent door §c{0} §6geraakt met een kogel.", String.class),
	GUNS_HIT_TOSHOOTER("§6Je hebt §c{0} §6geraakt met een kogel.", String.class),
	GUNS_BROKEN("&cJe wapen is kapot gegaan!"),
	GUNS_INFO(Arrays.asList("&9Durability: &a{0}", "&9Ammo: &a{1}&f/&c{2}"), Integer.class, Integer.class, Integer.class),
	GUNS_NOAMMO("§cJe hebt geen ammo meer!"),
	GUNS_RELOADING_MESSAGE("§6Je wapen is §csuccesvol §6herladen."),
	GUNS_RELOADING_TITLE("§eReloading..."),
	GUNS_RELOADING_SUBTITLE("§7Clickerdy click."),
	GUNS_NOTIFICATIONS_AMMO("§9Ammo: §a{0}§f/§c{1}", Integer.class, Integer.class),
	GUNS_NOTIFICATIONS_BROKEN("Je wapen is kapot gegaan!"),
	GUNS_NOTIFICATIONS_RELOADED("§6Je wapen is §csuccesvol §6herladen."),
	GUNS_NOTIFICATIONS_RELOADING("§6Je wapen wordt herladen..."),

	CHAT_FORMAT("&3[{0}&3] &8[{1}{2}&8] {3}{4}{5}: {6}", String.class, String.class, String.class, String.class, String.class, String.class, String.class),

	ADDONS_VIEW("Addons ({0}): {1}", Integer.class, String.class),
	ADDONS_SEPERATOR(",");

	private final @Getter(value = AccessLevel.PRIVATE) String message;
	private final @Getter(value = AccessLevel.PRIVATE) List<String> messageList;
	private final @Getter(value = AccessLevel.PRIVATE) Class<?>[] allowedNamespaces;
	private int characterLimit = -1;

	Message(String message, Class<?>... allowedNamespaces) {
		this.message = message;
		this.messageList = null;
		this.allowedNamespaces = allowedNamespaces;
	}

	Message(List<String> messageList, Class<?>... allowedNamespaces) {
		this.message = null;
		this.messageList = messageList;
		this.allowedNamespaces = allowedNamespaces;
	}

	private static @Getter
	final HashMap<Message, String> messageCache = new HashMap<>();
	private static @Getter
	final HashMap<Message, List<String>> alternativeCache = new HashMap<>();

	public static void loadAll() {
		messageCache.clear();
		alternativeCache.clear();

		Configuration configuration = Minetopia.getPlugin(Minetopia.class).getMessages();
		Arrays.stream(values()).forEach(o -> {
			String yamlPath = o.toString().toLowerCase().replaceAll("_", ".");
			if (!configuration.get().contains(yamlPath)) {
				configuration.get().set(yamlPath, o.message == null ? o.messageList : o.message);
				if (o.message == null)
					alternativeCache.put(o, o.messageList);
				else messageCache.put(o, o.message);
			} else {
				messageCache.put(o, configuration.get().getString(yamlPath));
			}
			configuration.save();
		});
	}

	public List<String> formatAsList(Object... namespaces) {
		if (this.getMessageList() == null) throw new InvalidMessageKeyException("Can't use List if config option is specifically made for String use.");

		try {
			verify(namespaces);
		} catch (InvalidMessageKeyException exception) {
			exception.printStackTrace();
			return Collections.singletonList("&c" + super.toString().replaceAll("_", "."));
		}

		List<String> defaultMessage = new ArrayList<>( this.getMessageList() );
		if (alternativeCache.containsKey(this))
			defaultMessage = alternativeCache.get(this);
		List<String> copy = new ArrayList<>( defaultMessage );
		for (int i = 0; i < namespaces.length; i++) {
			for (int j = 0; j < copy.size(); j++) {
				String line = defaultMessage.get(j);
				if (line.contains("{" + i + "}")) {
					System.out.println("[DEBUG] Found {" + i + "} in " + line + " replaced with " + namespaces[i]);
					line = line.replace("{" + i + "}", String.valueOf(namespaces[i]));
					defaultMessage.set(j, line);
					break;
				}
			}
		}

		return Text.colors(defaultMessage);
	}

	public String format(Object... namespaces) {
		try {
			verify(namespaces);
		} catch (InvalidMessageKeyException exception) {
			exception.printStackTrace();
			return "&c" + super.toString().replaceAll("_", ".").toLowerCase();
		}

		String defaultMessage = this.getMessage();
		if (messageCache.containsKey(this))
			defaultMessage = messageCache.get(this);
		for (int i = 0; i < namespaces.length; i++) {
			defaultMessage = defaultMessage.replace("{" + i + "}", String.valueOf(namespaces[i]));
		}

		String colorified = Text.colors(defaultMessage);
		if (characterLimit != -1 && colorified.length() > characterLimit)
			colorified = colorified.substring(0, characterLimit);
		return colorified;
	}

	public Message setLimit(int input) {
		this.characterLimit = input;
		return this;
	}

	public String raw() {
		return format();
	}

	protected void verify(Object[] input) throws InvalidMessageKeyException {
		if (this.getAllowedNamespaces().length != input.length) throw new InvalidMessageKeyException("The message namespaces don't match up to input variables. (Required: " + getAllowedNamespaces().length + "; Given: " + input.length + ")");

		int i = 0;

		for (Object object : input) {
			Class<?> clazz = object.getClass();
			Class<?> compareWith = this.getAllowedNamespaces()[i];

			if (!clazz.equals(compareWith)) {
				throw new InvalidMessageKeyException("Invalid namespace provided with message " + this.toString() + " class " + clazz.toString() + " is not equal to " + compareWith.toString());
			}

			i++;
		}

	}

}
