import java.util.*;

interface Step {
    void run();
}

interface Identity {
    void check();
}

class AIIdentity implements Identity {
    public void check() {
        System.out.println("AI is verifying identity");
    }
}

class HumanIdentity implements Identity {
    public void check() {
        System.out.println("Human is verifying identity");
    }
}

interface Environment {
    void check();
}

class AIEnvironment implements Environment {
    public void check() {
        System.out.println("AI is checking environment");
    }
}

interface Behaviour {
    void check();
}

class AIBehaviour implements Behaviour {
    public void check() {
        System.out.println("AI is monitoring behaviour");
    }
}

class HumanBehaviour implements Behaviour {
    public void check() {
        System.out.println("Human is monitoring behaviour");
    }
}

class IdentityStep implements Step {
    private final Identity identity;

    IdentityStep(Identity identity) {
        this.identity = identity;
    }

    public void run() {
        identity.check();
    }
}

class EnvironmentStep implements Step {
    private final Environment environment;

    EnvironmentStep(Environment environment) {
        this.environment = environment;
    }

    public void run() {
        environment.check();
    }
}

class BehaviourStep implements Step {
    private final Behaviour behaviour;

    BehaviourStep(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public void run() {
        behaviour.check();
    }
}

class Controller {
    private final List<Step> steps = new ArrayList<>();

    void addStep(Step step) {
        steps.add(step);
    }

    void start() {
        for (Step step : steps) {
            step.run();
        }
    }
}

public class main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.addStep(new IdentityStep(new AIIdentity()));
        controller.addStep(new EnvironmentStep(new AIEnvironment()));
        controller.addStep(new BehaviourStep(new HumanBehaviour()));
        controller.start();
    }
}
