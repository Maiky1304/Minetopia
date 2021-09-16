// Command /level

Commands.create().assertPlayer().handler(function (c) {
    c.sender().sendMessage(colorize("&3Jouw level is &bLevel " + MongoPlayerManager.getCache().get(c.sender().getUniqueId()).getLevel() + "&3."));
}).register(plugin, "level");