package com.creatokids.hajwithibraheem.Services.Chat;

import java.util.Random;

public class InnerResponse {

    public enum responseType{
        none,
        fun,
        teacherQuestion,
        introductoryMSG,
        askAboutAnythingElse,
        sayExcellent,
        noProblem,
        again
    }

    public static String getResponse(responseType type){
        String res = "";
        switch (type){
            case fun:
                res = getSomeFun();
                break;
            case sayExcellent:
                res = getExcellentMsg();
                break;
            case introductoryMSG:
                res = getIntroductoryMessage();
                break;
            case teacherQuestion:
                res = getTeacherQuestion();
                break;
            case askAboutAnythingElse:
                res = askAboutAnythingElse();
                break;
            case noProblem:
                res = sayNoProblem();
                break;
        }
        res = ""; // let the response empty for now
        return res;
    }

    private static String getSomeFun(){
        String[] funList = new String[]{
                "cool question, let's find out about it",
                "okay, let me blow your mind",
                "it's science time",
                "Let's look for it together",
                "excellent question",
                "I am glad you asked me about that",
                "well let me tell you about it",
                "Smart question",
                "let's explore it together",
                "let's go through it"};
        return funList[new Random().nextInt(funList.length)] + "! .";
    }

    private static String getTeacherQuestion(){
        String[] questions = new String[]{
                "Do you get it?",
                "Is it clear now?",
                "Does it make sense to you?",
                "Do you understand?",
                "Is it easy enough for you to understand",
                "Am I making sense?",
                "do you follow?",
                "Does that make sense?",
                "Did you catch all that?",
                "Am I explaining this clearly?",
                "Am I getting through to you here?"
        };
        return questions[new Random().nextInt(questions.length)];
    }

    private static String getIntroductoryMessage(){
        String[] selfIntro = new String[]{
                "Hello. I'm Jack your personal assistant, I can study with you.",
                "Hello, I’m Jack your personal assistant, we can study together.",
                "Hello. I'm Jack your smart tutor, I can study with you.",
                "Hi, I'm Jack. I can help you in your lessons.",
                "Hey, It's me, Jack. Let's study together.",
                "Hi, I'm Jack. Can we become friends? I can help you in your lessons."
        };

        String[] suggestAsking = new String[]{
                "You can ask me about energy",
                "I can tell you about potential energy",
                "If you‘re wondering about kinetic energy, I can tell you",
                "I can tell you how the potential energy is converted into kinetic energy",
                "If you want to know about roller coaster mechanics, you can ask me",
                "I can tell you about solar energy",
                "We can explore the sources of energy together",
                "You can ask me about the pendulum",
                "Try to ask about your lessons",
                "You can ask me about your lessons",
                "What did you learn about science today in school",
                "If you have trouble in your lessons, I can help you"
        };
        return selfIntro[new Random().nextInt(selfIntro.length)]
                + suggestAsking[new Random().nextInt(suggestAsking.length)];
    }

    private static String askAboutAnythingElse() {
        String[] moreQuestions = new String[]{
                "If you wanna ask for any thing else, Just ask me.",
                "I can help you in more lessons, try to ask.",
                "I know a lot about your lessons, And i'm always waiting for you.",
                "IF you need to know more, Just ask me."};
        return moreQuestions[new Random().nextInt(moreQuestions.length)];
    }

    private static String getExcellentMsg() {
        String[] excellentMsgs = new String[]{
                "Amazing!",
                "Astonishing!",
                "Awesome!",
                "Beautiful!",
                "BINGO!",
                "Bravo!",
                "Brilliant!",
                "Clever",
                "Cool",
                "Excellent!",
                "Exceptional!",
                "Extraordinary!",
                "Fantastic!",
                "Good thinking!",
                "Great answer!",
                "Hooray!",
                "Incredible!",
                "Looking good!",
                "Magnificent!",
                "Marvelous!",
                "Nice job!",
                "Now you've got it!",
                "Perfect!",
                "Phenomenal!",
                "Remarkable!",
                "Sensational!",
                "Spectacular!",
                "Stupendous!",
                "Super!",
                "Super Duper!",
                "Super Star!",
                "Super work!",
                "Superb!",
                "Sweet!",
                "Terrific!",
                "That's amazing!",
                "That's correct!",
                "Tremendous!",
                "Unbelievable!",
                "Very good!",
                "Very impressive!",
                "Way to go!",
                "We have a winner!",
                "Well done!",
                "What a genius!",
                "Wonderful!",
                "Wow!",
                "You got it!",
                "You just blew me away!",
                "You rock!",
                "You're a winner!",
                "You're on fire!"
        };
        return excellentMsgs[new Random().nextInt(excellentMsgs.length)];
    }

    private static String sayNoProblem(){
        String[] strings = new String[]{
                "No problem, Let's look for it together.",
                "No problem, Let's look for it together."
        };
        return strings[new Random().nextInt(strings.length)];
    }

    private static String getAgainResponse(){
        String[] strings = new String[]{
                "Let's see that again",
                "Let's see that again"
        };
        return strings[new Random().nextInt(strings.length)];
    }
}
