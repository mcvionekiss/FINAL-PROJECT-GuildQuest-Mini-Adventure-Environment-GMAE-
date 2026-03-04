package guildquest.app;

import guildquest.app.menu.MenuAction;
import guildquest.app.menu.CreateCampaignAction;
import guildquest.app.menu.ManageCampaignAction;
import guildquest.app.factory.GameObjectFactory;

import java.util.HashMap;
import java.util.Map;

import guildquest.enums.*;
import guildquest.model.*;

import guildquest.model.Character;

import java.util.*;

public class GuildQuestApp {
    private final Scanner in = new Scanner(System.in);
    private final GuildQuest system = new GuildQuest();
    private User currentUser;
    private Map<String, MenuAction> campaignActions;

    public GuildQuestApp() {
        initCampaignActions();
    }

    public void start() {
        seedData();
        mainLoop();
    }

    private void seedData() {
        // realms
        Realm earth = system.createRealm("Earth", "Default realm", 0);
        Realm mars = system.createRealm("Mars Colony", "Red planet settlement", 180); // +3h

        // users
        User a = system.registerUser("alice");
        User b = system.registerUser("bob");

        a.getSettings().setCurrentRealmId(earth.getId());
        b.getSettings().setCurrentRealmId(mars.getId());

        // starter character
        a.addCharacter("Kira", "Rogue", 3, 10);
    }

    private void mainLoop() {
        while (true) {
            System.out.println("\n=== GuildQuest ===  WorldTime: " + system.getWorldClock().now());
            System.out.println("1) Login as existing user");
            System.out.println("2) Register new user");
            System.out.println("3) Advance world clock minutes");
            System.out.println("0) Exit");
            System.out.print("> ");
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> advanceClock();
                case "0" -> { System.out.println("Bye."); return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private void login() {
        System.out.print("Username: ");
        String name = in.nextLine().trim();
        Optional<User> u = system.findUserByName(name);
        if (u.isEmpty()) { System.out.println("No such user."); return; }
        currentUser = u.get();
        userMenu();
    }

    private void register() {
        System.out.print("New username: ");
        String name = in.nextLine().trim();
        try {
            currentUser = system.registerUser(name);
            System.out.println("Created user: " + currentUser.getUsername());
            // set initial realm if any exists
            system.getRealms().stream().findFirst().ifPresent(r -> currentUser.getSettings().setCurrentRealmId(r.getId()));
            userMenu();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void advanceClock() {
        System.out.print("Minutes to advance: ");
        long m = Long.parseLong(in.nextLine().trim());
        system.getWorldClock().advanceMinutes(m);
        System.out.println("World time is now " + system.getWorldClock().now());
    }

    private void userMenu() {
        while (true) {
            System.out.println("\n--- User: " + currentUser.getUsername() + " ---");
            System.out.println("1) Settings");
            System.out.println("2) Realms");
            System.out.println("3) Campaigns");
            System.out.println("4) Characters/Inventory");
            System.out.println("5) Sharing");
            System.out.println("0) Logout");
            System.out.print("> ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1" -> settingsMenu();
                case "2" -> realmsMenu();
                case "3" -> campaignsMenu();
                case "4" -> charactersMenu();
                case "5" -> sharingMenu();
                case "0" -> { currentUser = null; return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    // -------- Settings --------
    private void settingsMenu() {
        while (true) {
            UserSettings s = currentUser.getSettings();
            Realm cur = s.getCurrentRealmId() == null ? null : system.getRealm(s.getCurrentRealmId());
            System.out.println("\n[Settings]");
            System.out.println("Current realm: " + (cur == null ? "(none)" : cur));
            System.out.println("Theme: " + s.getTheme());
            System.out.println("Time display: " + s.getTimeDisplayMode());
            System.out.println("1) Set current realm");
            System.out.println("2) Set time display mode");
            System.out.println("3) Set theme");
            System.out.println("0) Back");
            System.out.print("> ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1" -> {
                    Realm r = pickRealm();
                    if (r != null) s.setCurrentRealmId(r.getId());
                }
                case "2" -> {
                    System.out.println("Choose: WORLD / REALM_LOCAL / BOTH");
                    String v = in.nextLine().trim();
                    s.setTimeDisplayMode(TimeDisplayMode.valueOf(v));
                }
                case "3" -> {
                    System.out.println("Choose: CLASSIC / MODERN");
                    String v = in.nextLine().trim();
                    s.setTheme(ThemeType.valueOf(v));
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    // -------- Realms --------
    private void realmsMenu() {
        while (true) {
            System.out.println("\n[Realms]");
            listRealms();
            System.out.println("1) Create realm");
            System.out.println("0) Back");
            System.out.print("> ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1" -> createRealm();
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private void listRealms() {
        int i = 1;
        for (Realm r : system.getRealms()) {
            System.out.println(i++ + ") " + r.getId() + "  " + r);
        }
    }

    private void createRealm() {
        System.out.print("Name: ");
        String name = in.nextLine().trim();
        System.out.print("Description (optional): ");
        String desc = in.nextLine().trim();
        if (desc.isBlank()) desc = null;
        System.out.print("Offset minutes (e.g., 180 = +3h): ");
        int off = Integer.parseInt(in.nextLine().trim());
        Realm r = system.createRealm(name, desc, off);
        System.out.println("Created realm: " + r.getId());
    }

    private Realm pickRealm() {
        List<Realm> realms = new ArrayList<>(system.getRealms());
        if (realms.isEmpty()) { System.out.println("No realms."); return null; }
        for (int i = 0; i < realms.size(); i++) {
            System.out.println((i + 1) + ") " + realms.get(i));
        }
        System.out.print("Pick realm #: ");
        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
        if (idx < 0 || idx >= realms.size()) { System.out.println("Invalid."); return null; }
        return realms.get(idx);
    }

    // -------- Campaigns --------
    private void campaignsMenu() {
        while (true) {

            System.out.println("\n[Campaigns] Visible to you:");
            List<Campaign> visible =
                    this.system.listVisibleCampaignsFor(this.currentUser);

            for (int i = 0; i < visible.size(); ++i) {
                System.out.println(
                        (i + 1) + ") " +
                                visible.get(i).getId() + "  " +
                                visible.get(i)
                );
            }

            System.out.println("1) Create campaign");
            System.out.println("2) Manage a campaign");
            System.out.println("0) Back");
            System.out.print("> ");

            String choice = in.nextLine().trim();

            if (choice.equals("0")) {
                return;
            }

            MenuAction action = campaignActions.get(choice);

            if (action != null) {
                action.execute(this);
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    public void createCampaign() {
        System.out.print("Campaign name: ");
        String name = in.nextLine().trim();
        System.out.print("Visibility (PUBLIC/PRIVATE): ");
        VisibilityType v = VisibilityType.valueOf(in.nextLine().trim());
        Campaign c = GameObjectFactory.createCampaign(
                system,
                currentUser,
                name,
                v
        );
        System.out.println("Created campaign: " + c.getId());
    }

    private void printCampaignMenu(Campaign c) {
        System.out.println("\nCampaign: " + c.getName() + "  " + c.getId());
        System.out.println("Visibility: " + c.getVisibility() + "  Archived: " + c.isArchived());
        System.out.println("1) Rename");
        System.out.println("2) Toggle visibility");
        System.out.println("3) Archive");
        System.out.println("4) List events");
        System.out.println("5) Add event");
        System.out.println("6) Edit event");
        System.out.println("7) Remove event");
        System.out.println("8) Timeline view");
        System.out.println("0) Back");
        System.out.print("> ");
    }

    private void handleCampaignChoice(Campaign c, String ch, boolean canEdit) {

        if (!canEdit && Set.of("1","2","3","5","6","7").contains(ch)) {
            System.out.println("No permission to edit.");
            return;
        }

        switch (ch) {
            case "1" -> {
                System.out.print("New name: ");
                c.setName(in.nextLine().trim());
            }
            case "2" ->
                    c.toggleVisibility();
            case "3" -> c.setArchived(true);
            case "4" -> listEvents(c);
            case "5" -> addEvent(c);
            case "6" -> editEvent(c);
            case "7" -> removeEvent(c);
            case "8" -> timelineView(c);
            default -> System.out.println("Invalid.");
        }
    }

    public void manageCampaign() {
        System.out.print("Campaign UUID: ");
        UUID id = UUID.fromString(in.nextLine().trim());
        Optional<Campaign> oc = system.getCampaign(id);
        if (oc.isEmpty()) { System.out.println("Not found."); return; }

        boolean canEdit = system.canEditCampaign(currentUser, id) ||
                // allow owner management even if not in registry (should be)
                currentUser.getCampaigns().stream().anyMatch(c -> c.getId().equals(id));

        if (!canEdit) {
            System.out.println("You can view this campaign but cannot edit (need owner or COLLAB share).");
        }

        Campaign c = oc.get();
        while (true) {

            printCampaignMenu(c);

            String ch = in.nextLine().trim();

            if (ch.equals("0")) return;

            handleCampaignChoice(c, ch, canEdit);
        }
    }

    private void listEvents(Campaign c) {
        System.out.println("\nEvents:");
        for (QuestEvent e : c.getEvents()) {
            System.out.println("- " + e.getId() + "  " + formatEventForUser(e));
        }
    }

    private void addEvent(Campaign c) {
        System.out.print("Title: ");
        String title = in.nextLine().trim();
        WorldTime start = promptWorldTime("Start");
        System.out.print("Has end time? (y/n): ");
        String yn = in.nextLine().trim().toLowerCase();
        WorldTime end = null;
        if (yn.equals("y")) end = promptWorldTime("End");

        Realm realm = pickRealm();
        if (realm == null) return;

        System.out.print("Visibility (PUBLIC/PRIVATE): ");
        VisibilityType v = VisibilityType.valueOf(in.nextLine().trim());

        QuestEvent e = GameObjectFactory.createQuestEvent(
                title,
                start,
                end,
                realm,
                v
        );

        // optional: participant + reward
        if (!currentUser.getCharacters().isEmpty()) {
            System.out.print("Add a participant character? (y/n): ");
            if (in.nextLine().trim().equalsIgnoreCase("y")) {
                Character ch = pickCharacter();
                if (ch != null) e.addParticipant(ch);
            }
        }

        System.out.print("Add a reward item? (y/n): ");
        if (in.nextLine().trim().equalsIgnoreCase("y")) {
            Item item = promptItem();
            e.addRewardItem(item);
        }

        system.createQuestEvent(currentUser, c.getId(), e);
        System.out.println("Added event: " + e.getId());
    }

    private void editEvent(Campaign c) {
        System.out.print("Event UUID: ");
        UUID id = UUID.fromString(in.nextLine().trim());
        QuestEvent e;
        try { e = c.findEvent(id); }
        catch (Exception ex) { System.out.println("Not found."); return; }

        while (true) {
            System.out.println("\nEvent: " + e.getTitle() + " " + e.getId());
            System.out.println("1) Rename");
            System.out.println("2) Set start");
            System.out.println("3) Set end (or clear)");
            System.out.println("4) Set realm");
            System.out.println("5) Toggle visibility");
            System.out.println("6) Complete event (apply rewards to a character)");
            System.out.println("0) Back");
            System.out.print("> ");
            String ch = in.nextLine().trim();
            switch (ch) {
                case "1" -> { System.out.print("New title: "); e.setTitle(in.nextLine().trim()); }
                case "2" -> e.setStart(promptWorldTime("Start"));
                case "3" -> {
                    System.out.print("Set end? (y/n, n clears): ");
                    if (in.nextLine().trim().equalsIgnoreCase("y")) e.setEnd(promptWorldTime("End"));
                    else e.setEnd(null);
                }
                case "4" -> {
                    Realm r = pickRealm();
                    if (r != null) e.setRealm(r);
                }
                case "5" -> e.setVisibility(e.getVisibility() == VisibilityType.PUBLIC ? VisibilityType.PRIVATE : VisibilityType.PUBLIC);
                case "6" -> completeEventApplyRewards(e);
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private void removeEvent(Campaign c) {
        System.out.print("Event UUID: ");
        UUID id = UUID.fromString(in.nextLine().trim());
        boolean ok = c.removeQuestEvent(id);
        System.out.println(ok ? "Removed." : "Not found.");
    }

    private void timelineView(Campaign c) {
        System.out.println("Period: DAY/WEEK/MONTH/YEAR");
        TimePeriodType p = TimePeriodType.valueOf(in.nextLine().trim());
        WorldTime anchor = promptWorldTime("Anchor (start of range)");
        List<QuestEvent> events = c.timeline(p, anchor);
        System.out.println("\nTimeline " + p + " starting " + anchor + ":");
        for (QuestEvent e : events) {
            System.out.println("- " + e.getId() + "  " + formatEventForUser(e));
        }
    }

    // -------- Characters --------
    private void charactersMenu() {
        while (true) {
            System.out.println("\n[Characters]");
            listCharacters();
            System.out.println("1) Create character");
            System.out.println("2) Add item to a character inventory");
            System.out.println("3) Remove item from inventory");
            System.out.println("0) Back");
            System.out.print("> ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1" -> createCharacter();
                case "2" -> addItemToCharacter();
                case "3" -> removeItemFromCharacter();
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private void listCharacters() {
        if (currentUser.getCharacters().isEmpty()) {
            System.out.println("(none)");
            return;
        }
        for (int i = 0; i < currentUser.getCharacters().size(); i++) {
            Character ch = currentUser.getCharacters().get(i);
            System.out.println((i + 1) + ") " + ch.getId() + "  " + ch);
            for (Item it : ch.getInventory().getItems()) {
                System.out.println("    - " + it.getId() + "  " + it);
            }
        }
    }

    private void createCharacter() {
        System.out.print("Name: ");
        String name = in.nextLine().trim();
        System.out.print("Class: ");
        String clazz = in.nextLine().trim();
        System.out.print("Level: ");
        int lvl = Integer.parseInt(in.nextLine().trim());
        System.out.print("Inventory capacity: ");
        int cap = Integer.parseInt(in.nextLine().trim());
        Character ch = GameObjectFactory.createCharacter(
                currentUser,
                name,
                clazz,
                lvl,
                cap
        );
        System.out.println("Created: " + ch.getId());
    }

    private Character pickCharacter() {
        List<Character> chars = currentUser.getCharacters();
        if (chars.isEmpty()) { System.out.println("No characters."); return null; }
        for (int i = 0; i < chars.size(); i++) {
            System.out.println((i + 1) + ") " + chars.get(i));
        }
        System.out.print("Pick #: ");
        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
        if (idx < 0 || idx >= chars.size()) { System.out.println("Invalid."); return null; }
        return chars.get(idx);
    }

    private void addItemToCharacter() {
        Character ch = pickCharacter();
        if (ch == null) return;
        Item it = promptItem();
        boolean ok = ch.getInventory().addItem(it);
        System.out.println(ok ? "Added." : "Inventory full.");
    }

    private void removeItemFromCharacter() {
        Character ch = pickCharacter();
        if (ch == null) return;
        System.out.print("Item UUID: ");
        UUID id = UUID.fromString(in.nextLine().trim());
        boolean ok = ch.getInventory().removeItemById(id);
        System.out.println(ok ? "Removed." : "Not found.");
    }

    private void completeEventApplyRewards(QuestEvent e) {
        if (e.getRewardItems().isEmpty()) {
            System.out.println("No rewards on this event.");
            return;
        }
        Character ch = pickCharacter();
        if (ch == null) return;

        for (Item it : e.getRewardItems()) {
            boolean ok = ch.getInventory().addItem(it);
            System.out.println(ok ? ("Rewarded: " + it.getName()) : ("Inventory full; could not add: " + it.getName()));
        }
    }

    // -------- Sharing --------
    private void sharingMenu() {
        while (true) {
            System.out.println("\n[Sharing]");
            System.out.println("1) Share a private campaign");
            System.out.println("2) Share a quest event");
            System.out.println("0) Back");
            System.out.print("> ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1" -> shareCampaignFlow();
                case "2" -> shareEventFlow();
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private void shareCampaignFlow() {
        System.out.print("Owned campaign UUID: ");
        UUID cid = UUID.fromString(in.nextLine().trim());

        Campaign owned;
        try { owned = currentUser.findOwnedCampaign(cid); }
        catch (Exception ex) { System.out.println("Not found or not owned."); return; }

        if (owned.getVisibility() == VisibilityType.PUBLIC) {
            System.out.println("Campaign is public; sharing not needed.");
            return;
        }

        System.out.print("Recipient username: ");
        String uname = in.nextLine().trim();
        Optional<User> rec = system.findUserByName(uname);
        if (rec.isEmpty()) { System.out.println("No such user."); return; }

        System.out.print("Permission (VIEW_ONLY/COLLABORATIVE): ");
        PermissionType p = PermissionType.valueOf(in.nextLine().trim());
        system.shareCampaign(currentUser, cid, rec.get(), p);
        System.out.println("Shared.");
    }

    private void shareEventFlow() {
        System.out.print("Event UUID: ");
        UUID eid = UUID.fromString(in.nextLine().trim());
        System.out.print("Recipient username: ");
        String uname = in.nextLine().trim();
        Optional<User> rec = system.findUserByName(uname);
        if (rec.isEmpty()) { System.out.println("No such user."); return; }
        try {
            system.shareEvent(currentUser, eid, rec.get());
            System.out.println("Shared event.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // -------- Helpers --------
    private WorldTime promptWorldTime(String label) {
        System.out.println(label + " time:");
        System.out.print("  days: "); int d = Integer.parseInt(in.nextLine().trim());
        System.out.print("  hours: "); int h = Integer.parseInt(in.nextLine().trim());
        System.out.print("  minutes: "); int m = Integer.parseInt(in.nextLine().trim());
        return WorldTime.of(d, h, m);
    }

    private Item promptItem() {
        System.out.print("Item name: ");
        String name = in.nextLine().trim();
        System.out.print("Rarity (COMMON/UNCOMMON/RARE/EPIC/LEGENDARY): ");
        RarityType r = RarityType.valueOf(in.nextLine().trim());
        System.out.print("Description (optional): ");
        String desc = in.nextLine().trim();
        if (desc.isBlank()) desc = null;
        return GameObjectFactory.createItem(name, r, desc);
    }

    private String formatEventForUser(QuestEvent e) {
        TimeDisplayMode mode = currentUser.getSettings().getTimeDisplayMode();
        String world = e.getStart().toString() + (e.getEnd() == null ? "" : " -> " + e.getEnd());
        String local = e.getRealm().toLocalTime(e.getStart()).toString()
                + (e.getEnd() == null ? "" : " -> " + e.getRealm().toLocalTime(e.getEnd()).toString());

        return switch (mode) {
            case WORLD -> e.getTitle() + " | WORLD " + world + " | Realm " + e.getRealm().getName();
            case REALM_LOCAL -> e.getTitle() + " | LOCAL " + local + " | Realm " + e.getRealm().getName();
            case BOTH -> e.getTitle() + " | WORLD " + world + " | LOCAL " + local + " | Realm " + e.getRealm().getName();
        };
    }

    private void initCampaignActions() {
        campaignActions = new HashMap<>();

        campaignActions.put("1", new CreateCampaignAction());
        campaignActions.put("2", new ManageCampaignAction());
    }
}
