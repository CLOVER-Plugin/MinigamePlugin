package yd.kingdom.main;

import org.bukkit.plugin.java.JavaPlugin;
import yd.kingdom.main.commands.*;
import yd.kingdom.main.game.*;
import yd.kingdom.main.listener.*;
import yd.kingdom.main.manager.round2.RoundTwoManager;
import yd.kingdom.main.util.LocationUtil;

public class Main extends JavaPlugin {
    private static Main instance;
    private GameManager gameManager;
    private ItemManager itemManager;
    private RoundTwoManager roundTwoManager;
    private UnlimitedChestManager unlimitedChestManager;
    private RopeManager ropeManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        LocationUtil.loadFromConfig(this);
        SoccerBallManager.getInstance().startPhysics(this);

        this.roundTwoManager = new RoundTwoManager();
        getServer().getPluginManager().registerEvents(roundTwoManager, this);

        this.unlimitedChestManager = new UnlimitedChestManager(this);
        getServer().getPluginManager().registerEvents(unlimitedChestManager, this);

        this.ropeManager = RopeManager.getInstance();
        getServer().getPluginManager().registerEvents(ropeManager, this);

        this.gameManager = new GameManager(this);
        this.itemManager = new ItemManager(this);

        getCommand("아이템").setExecutor(new ItemCommand());
        getCommand("팀").setExecutor(new TeamSetupCommand());
        getCommand("그림").setExecutor(new DefendCommand());
        getCommand("방해").setExecutor(new AttackCommand());
        getCommand("축구공").setExecutor(new SoccerBallCommand());
        getCommand("골키퍼").setExecutor(new GoalkeeperCommand());
        getCommand("수비수").setExecutor(new DefenderCommand());
        getCommand("공격수").setExecutor(new AttackerCommand());
        getCommand("라운드2시작").setExecutor(new RoundTwoStartCommand());
        getCommand("라운드2종료").setExecutor(new RoundTwoEndCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());

        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new TeamChatListener(), this);
        getServer().getPluginManager().registerEvents(new BallKickListener(), this);
        getServer().getPluginManager().registerEvents(new PositionListener(), this);
        getServer().getPluginManager().registerEvents(new TeamDefendListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakDropListener(), this);

        getLogger().info("Minigame 플러그인 활성화됨");
    }

    @Override
    public void onDisable() {
        getLogger().info("Minigame 플러그인 비활성화됨");
    }

    public static Main getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public RoundTwoManager getRoundTwoManager() {
        return roundTwoManager;
    }
}