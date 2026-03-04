package guildquest.app.menu;

import guildquest.app.GuildQuestApp;

public class CreateCampaignAction implements MenuAction {

    @Override
    public void execute(GuildQuestApp app) {
        app.createCampaign();
    }
}