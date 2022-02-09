import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

public class Main {
    public static void main(String[] args) {
        PlatformConfiguration config  = PlatformConfiguration.getDefaultNoGui();

        // Lab1
        //config.addComponent("Lab1.JobAgentBDI.class");

        //Lab2
        //config.addComponent("Lab2.JobAgentBDI.class");

        //Lab3
        //config.addComponent("Lab3.JobAgentBDI.class");

        //Lab4
        config.addComponent("Lab4.JobAgentBDI.class");
        config.addComponent("Lab4.GUIAgentBDI.class");
        config.addComponent("Lab4.GUIAgentBDI.class");

        Starter.createPlatform(config).get();
    }
}
