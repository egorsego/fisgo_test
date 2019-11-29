package hub_emulator.response.repository;

import hub_emulator.json.purchase.Tags;

import java.util.ArrayList;

public class RepositoryAgentTags {

    private RepositoryAgentTags() {
        throw new IllegalStateException("Utility class");
    }

    public static ArrayList<Tags> getAgent1() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 1));
        agent.add(new Tags(1075, "+79500001075"));
        agent.add(new Tags(1044, "2323232323"));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1026, "ВАСЯVASYA!)^№;%:634("));
        agent.add(new Tags(1005, "ADDRESSАдресс!№;%*?"));
        agent.add(new Tags(1016, "1111222200"));
        agent.add(new Tags(1171, "+79500354890"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getAgent2() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 2));
        agent.add(new Tags(1075, "+79500001075"));
        agent.add(new Tags(1044, "2323232323"));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1026, "ВАСЯVASYA!)^№;%:634("));
        agent.add(new Tags(1005, "ADDRESSАдресс!№;%*?"));
        agent.add(new Tags(1016, "000011112222"));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getAgent4() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 4));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1074, "+79500001073"));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getAgent8() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 8));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1074, "+79500001073"));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getAgent16() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 16));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getAgent32() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 32));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getAgent64() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 64));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    //__________________________________________________________________________________________________________________
    //                                                  НЕКОРРЕКТНЫЕ
    //__________________________________________________________________________________________________________________

    public static ArrayList<Tags> getIncorrectAgent1() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 1));
        agent.add(new Tags(1075, "+79500001075"));
        agent.add(new Tags(1044, "2323232323"));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1026, "ВАСЯVASYA!)^№;%:634("));
        agent.add(new Tags(1005, "ADDRESSАдресс!№;%*?"));
        agent.add(new Tags(1016, "1111222200"));
        agent.add(new Tags(1171, "+79500354890"));

        return agent;
    }

    public static ArrayList<Tags> getIncorrectAgent2() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 2));
        agent.add(new Tags(1075, "+79500001075"));
        agent.add(new Tags(1044, "2323232323"));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1026, "ВАСЯVASYA!)^№;%:634("));
        agent.add(new Tags(1016, "000011112222"));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getIncorrectAgent4() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 4));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1171, "+79500001171"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getIncorrectAgent8() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 8));
        agent.add(new Tags(1073, "+79500001073"));
        agent.add(new Tags(1074, "+79500001073"));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getIncorrectAgent16() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 16));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }

    public static ArrayList<Tags> getIncorrectAgent32() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 32));
        agent.add(new Tags(1171, "+79500001171"));

        return agent;
    }

    public static ArrayList<Tags> getIncorrectAgent64() {
        ArrayList<Tags> agent = new ArrayList<>();

        agent.add(new Tags(1057, 64));
        agent.add(new Tags(1226, "111122223333"));

        return agent;
    }


}
