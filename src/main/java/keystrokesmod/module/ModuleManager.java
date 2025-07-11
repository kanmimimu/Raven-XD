package keystrokesmod.module;


import keystrokesmod.module.impl.client.*;

import keystrokesmod.module.impl.combat.*;

import keystrokesmod.module.impl.exploit.*;

import keystrokesmod.module.impl.fun.*;

import keystrokesmod.module.impl.minigames.*;

import keystrokesmod.module.impl.movement.*;

import keystrokesmod.module.impl.other.*;

import keystrokesmod.module.impl.player.*;

import keystrokesmod.module.impl.render.*;

import keystrokesmod.module.impl.world.*;

import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;

import java.util.Comparator;

import java.util.List;


public class ModuleManager {

    public static List<Module> organizedModules = new ArrayList<>();

    public static Module longJump;

    public static Blink blink;

    public static Module nameHider;

    public static Module fastPlace;

    public static MurderMystery murderMystery;

    public static AntiFireball antiFireball;

    public static BedAura bedAura;

    public static FastMine fastMine;

    public static Module antiShuffle;

    public static Module commandLine;

    public static Module antiBot;

    public static NoSlow noSlow;

    public static KillAura killAura;

    public static AutoClicker autoClicker;

    public static HitBox hitBox;

    public static Reach reach;

    public static BedESP bedESP;

    public static HUD hud;

    public static Timer timer;

    public static Fly fly;

    public static MoreKB moreKB;

    public static Potions potions;

    public static NoFall noFall;

    public static PlayerESP playerESP;

    public static SafeWalk safeWalk;

    public static KeepSprint keepSprint;

    public static Velocity velocity;

    public static Tower tower;

    public static BedWars bedwars;

    public static Speed speed;

    public static InvManager invManager;

    public static Scaffold scaffold;

    public static AntiVoid antiVoid;

    public static Criticals criticals;

    public static TimerRange timerRange;

    public static TargetStrafe targetStrafe;

    public static AutoHeal autoHeal;

    public static HitSelect hitSelect;

    public static NoHurtCam noHurtCam;

    public static NoCameraClip noCameraClip;

    public static AutoPlay autoPlay;

    public static CustomName customName;

    public static CustomFOV customFOV;

    public static CommandChat commandChat;

    public static Phase phase;

    public static PingSpoof pingSpoof;

    public static NoBackground noBackground;

    public static BlockIn blockIn;

    public static Backtrack backtrack;

    public static Particles particles;

    public static RecordClick recordClick;

    public static ClickRecorder clickRecorder;

    public static InfiniteAura infiniteAura;

    public static LegitScaffold legitScaffold;

    public static FreeLook freeLook;

    public static Step step;

    public static Animations animations;

    public static ChestStealer chestStealer;

    public static Sprint sprint;

    public static RotationHandler rotationHandler;

    public static CustomCape customCape;

    public static ClientSpoofer clientSpoofer;

    public static BlockHit blockHit;

    public static FullBright fullBright;

    public static ModSpoofer modSpoofer;

    public static Panic panic;

    public static SlotHandler slotHandler;

    public static StaffDetector staffDetector;

    public static AutoRespawn autoRespawn;

    public static Clutch clutch;

    public static Ambience ambience;

    public static DynamicManager dynamicManager;

    public static Disabler disabler;

    public static BridgeAssist bridgeAssist;

    public static Watermark watermark;

    public static RightClicker rightClicker;

    public static Notifications notifications;

    public static WallClimb wallClimb;

    public static Jesus jesus;

    public static ExploitFixer exploitFixer;

    public static AutoRegister autoRegister;

    public static NoteBot noteBot;

    public static ViewPackets viewPackets;

    public static RageBot rageBot;

    public static HitLog hitLog;

    public static LagRange lagRange;

    public static FakePotion fakePotion;

    public static NoWeb noWeb;

    public static ProjectileAimBot projectileAimBot;

    public static AutoWeb autoWeb;

    public static BlockOut blockOut;

    public static GhostBlock ghostBlock;

    public static AirStuck airStuck;

    public static TargetHUD targetHUD;

    public static TargetESP targetESP;

    public static Language language;

    public static BedDefender bedDefender;

    public static ChestAura chestAura;

    public static AutoRod autoRod;

// public static AbilitiesBedWars abilitiesBedWars;

    public static ClientTheme clientTheme;

    public static AutoChest autoChest;

    public static Teleport teleport;

    public static AntiFalseFlag antiFalseFlag;

    public static ViaVersionFix viaVersionFix;

    public static AutoGapple autoGapple;

    public static RemoteShop remoteShop;

    public static Regen regen;

    public static ChatAI chatAI;

    public static keystrokesmod.module.impl.render.ArrayList arrayList;

    public static InvMove invMove;

    public static MotionCamera motionCamera;


    static List<Module> modules = new ArrayList<>();


    private static double getWidth(@NotNull Module module) {

        String text = module.getPrettyName()

                + ((HUD.showInfo.isToggled() && !module.getPrettyInfo().isEmpty()) ? " " + module.getPrettyInfo() : "");

        return HUD.getFontRenderer().width(HUD.lowercase.isToggled() ? text.toLowerCase() : text);

    }


    public static void sort() {

        if (HUD.alphabeticalSort.isToggled()) {

            organizedModules.sort(Comparator.comparing(Module::getPrettyName));

        } else {

            organizedModules.sort((c1, c2) -> Double.compare(getWidth(c2), getWidth(c1)));

        }

    }


    public void register() {


// client

        this.addModule(commandChat = new CommandChat());

        this.addModule(commandLine = new CommandLine());

        this.addModule(dynamicManager = new DynamicManager());

        this.addModule(new Gui());

// this.addModule(new NyaProxy());

        this.addModule(new Settings());

        this.addModule(new MiddleClick());

        this.addModule(notifications = new Notifications());

        this.addModule(new DiscordRpc());

        this.addModule(language = new Language());


// combat

        this.addModule(new AimAssist());

        this.addModule(autoClicker = new AutoClicker());

        this.addModule(rightClicker = new RightClicker());

        this.addModule(blockHit = new BlockHit());

        this.addModule(new BurstClicker());

        this.addModule(new ClickAssist());

        this.addModule(criticals = new Criticals());

        this.addModule(hitBox = new HitBox());

        this.addModule(hitSelect = new HitSelect());

        this.addModule(killAura = new KillAura());

        this.addModule(rageBot = new RageBot());

        this.addModule(lagRange = new LagRange());

        this.addModule(moreKB = new MoreKB());

        this.addModule(reach = new Reach());

        this.addModule(new RodAimbot());

        this.addModule(timerRange = new TimerRange());

        this.addModule(velocity = new Velocity());

        this.addModule(projectileAimBot = new ProjectileAimBot());

        this.addModule(airStuck = new AirStuck());

        this.addModule(autoRod = new AutoRod());

        this.addModule(autoGapple = new AutoGapple());


// fun

        this.addModule(new ExtraBobbing());

        this.addModule(new FlameTrail());

        this.addModule(new SlyPort());

        this.addModule(new AntiAim());

        this.addModule(hitLog = new HitLog());

        this.addModule(noteBot = new NoteBot());

        this.addModule(blockOut = new BlockOut());

// this.addModule(abilitiesBedWars = new AbilitiesBedWars());

        this.addModule(new Yeet());


// minigames

        this.addModule(new AutoWho());

        this.addModule(bedwars = new BedWars());

        this.addModule(new BridgeInfo());

        this.addModule(new DuelsStats());

        this.addModule(murderMystery = new MurderMystery());

        this.addModule(new SumoFences());


// movement

        this.addModule(fly = new Fly());

        this.addModule(invMove = new InvMove());

        this.addModule(keepSprint = new KeepSprint());

        this.addModule(longJump = new LongJump());

        this.addModule(noSlow = new NoSlow());

        this.addModule(phase = new Phase());

        this.addModule(speed = new Speed());

        this.addModule(sprint = new Sprint());

        this.addModule(step = new Step());

        this.addModule(new StopMotion());

        this.addModule(targetStrafe = new TargetStrafe());

        this.addModule(timer = new Timer());

        this.addModule(new VClip());

        this.addModule(wallClimb = new WallClimb());

        this.addModule(jesus = new Jesus());

        this.addModule(noWeb = new NoWeb());

        this.addModule(new SaveMoveKeys());

        this.addModule(teleport = new Teleport());


// other

        this.addModule(new Anticheat());

        this.addModule(autoPlay = new AutoPlay());

        this.addModule(autoRespawn = new AutoRespawn());

        this.addModule(clickRecorder = new ClickRecorder());

        this.addModule(new FakeChat());

        this.addModule(new LatencyAlerts());

        this.addModule(nameHider = new NameHider());

        this.addModule(panic = new Panic());

        this.addModule(recordClick = new RecordClick());

        this.addModule(rotationHandler = new RotationHandler());

        this.addModule(new ScreenshotHelper());

        this.addModule(slotHandler = new SlotHandler());

        this.addModule(staffDetector = new StaffDetector());

        this.addModule(new BedProximityAlert());

        this.addModule(autoRegister = new AutoRegister());

        this.addModule(viewPackets = new ViewPackets());

        this.addModule(new FlagDetector());

        this.addModule(chatAI = new ChatAI());

        this.addModule(new Test());


// player

        this.addModule(new AntiAFK());

        this.addModule(antiFireball = new AntiFireball());

        this.addModule(antiVoid = new AntiVoid());

        this.addModule(autoHeal = new AutoHeal());

        this.addModule(new AutoJump());

        this.addModule(new AutoPot());

        this.addModule(new AutoSwap());

        this.addModule(backtrack = new Backtrack());

        this.addModule(blink = new Blink());

        this.addModule(chestStealer = new ChestStealer());

        this.addModule(new NoJumpDelay());

        this.addModule(new FakeLag());

        this.addModule(new Freecam());

        this.addModule(invManager = new InvManager());

        this.addModule(noFall = new NoFall());

        this.addModule(new NoRotate());

        this.addModule(fakePotion = new FakePotion());

        this.addModule(autoWeb = new AutoWeb());

        this.addModule(chestAura = new ChestAura());

        this.addModule(autoChest = new AutoChest());


// render

        this.addModule(ambience = new Ambience());

        this.addModule(animations = new Animations());

        this.addModule(antiShuffle = new AntiShuffle());

        this.addModule(bedESP = new BedESP());

        this.addModule(new BreakProgress());

        this.addModule(new Chams());

        this.addModule(new ChestESP());

        this.addModule(customCape = new CustomCape());

        this.addModule(customName = new CustomName());

        this.addModule(customFOV = new CustomFOV());

        this.addModule(freeLook = new FreeLook());

        this.addModule(fullBright = new FullBright());

        this.addModule(hud = new HUD());

        this.addModule(new Indicators());

        this.addModule(new ItemESP());

        this.addModule(new MobESP());

        this.addModule(new Nametags());

        this.addModule(noBackground = new NoBackground());

        this.addModule(noCameraClip = new NoCameraClip());

        this.addModule(noHurtCam = new NoHurtCam());

        this.addModule(particles = new Particles());

        this.addModule(playerESP = new PlayerESP());

        this.addModule(potions = new Potions());

        this.addModule(new Radar());

        this.addModule(new Shaders());

        this.addModule(targetHUD = new TargetHUD());

        this.addModule(targetESP = new TargetESP());

        this.addModule(new Tracers());

        this.addModule(new Trajectories());

        this.addModule(new Xray());

        this.addModule(new BedPlates());

        this.addModule(watermark = new Watermark());

        this.addModule(new Explosions());

        this.addModule(new KillMessage());

        this.addModule(clientTheme = new ClientTheme());

        this.addModule(arrayList = new keystrokesmod.module.impl.render.ArrayList());

        this.addModule(motionCamera = new MotionCamera());



// world

        this.addModule(antiBot = new AntiBot());

        this.addModule(new AutoPlace());

        this.addModule(new AutoTool());

        this.addModule(new AutoWeapon());

        this.addModule(bedAura = new BedAura());

        this.addModule(blockIn = new BlockIn());

        this.addModule(bridgeAssist = new BridgeAssist());

        this.addModule(clutch = new Clutch());

        this.addModule(fastMine = new FastMine());

        this.addModule(fastPlace = new FastPlace());

        this.addModule(legitScaffold = new LegitScaffold());

        this.addModule(safeWalk = new SafeWalk());

        this.addModule(scaffold = new Scaffold());

        this.addModule(tower = new Tower());

        this.addModule(bedDefender = new BedDefender());


// exploit

        this.addModule(clientSpoofer = new ClientSpoofer());

        this.addModule(disabler = new Disabler());

        this.addModule(infiniteAura = new InfiniteAura());

        this.addModule(modSpoofer = new ModSpoofer());

        this.addModule(pingSpoof = new PingSpoof());

        this.addModule(exploitFixer = new ExploitFixer());

        this.addModule(ghostBlock = new GhostBlock());

        this.addModule(antiFalseFlag = new AntiFalseFlag());

        this.addModule(viaVersionFix = new ViaVersionFix());

        this.addModule(remoteShop = new RemoteShop());

        this.addModule(regen = new Regen());


// enable

        antiBot.enable();

        commandChat.enable();

        notifications.enable();

        clientTheme.enable();

        modules.sort(Comparator.comparing(Module::getPrettyName));

    }


    public void addModule(Module m) {

        modules.add(m);

    }


    public List<Module> getModules() {

        return modules;

    }


    public List<Module> inCategory(Module.category category) {

        ArrayList<Module> categoryML = new ArrayList<>();


        for (Module mod : this.getModules()) {

            if (mod.moduleCategory().equals(category)) {

                categoryML.add(mod);

            }

        }


        return categoryML;

    }


    public Module getModule(String moduleName) {

        for (Module module : modules) {

            if (module.getName().equals(moduleName)) {

                return module;

            }

        }

        return null;

    }

}