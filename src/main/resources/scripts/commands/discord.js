// Command /discord

Commands.create().assertPlayer().handler(function (c) {
    c.sender().sendMessage("Typ hier het bericht wat je wilt dat de speler krijgt!");
}).register(plugin, "discord");