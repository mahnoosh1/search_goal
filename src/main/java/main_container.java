

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class main_container {

    Runtime rt;
    ContainerController container;
    public double findAngle(double p0x,double p0y,double p1x,double p1y,double p2x,double p2y) {
        double a = Math.pow(p1x-p0x,2) + Math.pow(p1y-p0y,2),
                b = Math.pow(p1x-p2x,2) + Math.pow(p1y-p2y,2),
                c = Math.pow(p2x-p0x,2) + Math.pow(p2y - p0y, 2);
        return 57.2958*Math.acos( (a+b-c) / Math.sqrt(4*a*b) );
    }
    public static void main(String[] args) {

        main_container a = new main_container();

        a.initMainContainerInPlatform("localhost", "9888", "MainContainer");
        a.startAgentInPlatform("coordinator", "agents.coordinator");
        a.startAgentInPlatform("left_agent", "agents.left_agent");
        a.startAgentInPlatform("right_agent", "agents.right_agent");
        a.startAgentInPlatform("middle_agent", "agents.middle_agent");

    }

    public ContainerController initContainerInPlatform(String host, String port, String containerName) {

        this.rt = Runtime.instance();

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);

        ContainerController container = rt.createAgentContainer(profile);
        return container;

    }

    public void initMainContainerInPlatform(String host, String port, String containerName) {

        this.rt = Runtime.instance();
        Profile prof = new ProfileImpl();
        prof.setParameter(Profile.CONTAINER_NAME, containerName);
        prof.setParameter(Profile.MAIN_HOST, host);
        prof.setParameter(Profile.MAIN_PORT, port);
        prof.setParameter(Profile.MAIN, "true");
        prof.setParameter(Profile.GUI, "true");

        this.container = rt.createMainContainer(prof);
        rt.setCloseVM(true);
    }

    public void startAgentInPlatform(String name, String classpath) {
        try {
            AgentController ac = container.createNewAgent( name, classpath, new Object[0]);

            ac.start();

        } catch (Exception e)  {
            e.printStackTrace();
        }
    }




}