package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.models.OnlineTraining;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TrainingPlanInterface extends TrainerAccountInterface {

    private final ChoosingHelper choosingHelper;

    private ReplyKeyboardMarkup athletesMarkup;

    private boolean adding;

    private boolean cancelling;

    public TrainingPlanInterface(TrainerHelper helper) {
        super(helper);
        this.choosingHelper = new ChoosingHelper();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ChoosingHelper {

        private Map<String, Athlete> athletes = new HashMap<>();

        private Map<String, OnlineTraining> trainings = new HashMap<>();

        private Athlete athlete;

        private OnlineTraining training;

        private boolean date;

        private boolean time;

    }

    public SendResponse execute(Long chatId, Message message) {
        String txt = message.text();
        if (!getHelper().isMakesTrainingPlan()) {
            getHelper().setMakesTrainingPlan(true);
            return sendMenu(chatId);
        } else if (adding) {
            return fillOnlineTraining(chatId, txt);
        } else if (cancelling) {
            return cancelTraining(chatId, txt);
        }
        return switching(chatId, message);
    }

    @Override
    protected SendResponse switching(Long chatId, Message message) {
        String txt = message.text();
        switch (txt) {
            case "Записать на тренировку" -> {
                return startAdding(chatId);
            }
            case "Отменить тренировку" -> {
                return startCancelling(chatId);
            }
            case "Посмотреть общее расписание" -> {
                return sendSchedule(chatId);
            }
            case "Назад" -> {
                return stop(chatId);
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse fillOnlineTraining(Long chatId, String txt) {
        OnlineTraining onlineTraining = choosingHelper.getTraining();
        if (txt.equals("Назад")) {
            return goBack(chatId, onlineTraining);
        }
        if (onlineTraining.getAthlete() == null) {
            Athlete athlete = choosingHelper.getAthletes().get(txt);
            if (athlete != null) {
                onlineTraining.setAthlete(athlete);
                onlineTraining.setDescription(athlete.getFirstName() + " " + athlete.getSecondName());
            }
            return sendDates(chatId);
        } else if (onlineTraining.getDate() == null) {
            onlineTraining.setDate(LocalDate.parse(txt));
            onlineTraining.setDescription(onlineTraining.getDescription() + " " + txt);
            return sendTimes(chatId);
        } else {
            onlineTraining.setDescription(onlineTraining.getDescription() + " " + txt);
            onlineTraining.setTime(LocalTime.parse(txt));
            onlineTrainingService().post(onlineTraining);
            adding = false;
            sender().sendResponse(new SendMessage(chatId, "Вы записали на тренировку атлета " + onlineTraining.getAthlete().getFirstName()));
            sender().sendResponse(new SendMessage(onlineTraining.getAthlete().getId(),
                    "Вы записаны на тренировку " + onlineTraining.getDate() + " в " + onlineTraining.getTime()));
            choosingHelper.getAthletes().clear();
            choosingHelper.setTraining(null);
            return sendMenu(chatId);
        }
    }

    protected SendResponse stop(Long chatId) {
        getHelper().setMakesTrainingPlan(false);
        return trainerAccountBlockable().uniqueStartBlockMessage(chatId);
    }

    private SendResponse goBack(Long chatId, OnlineTraining training) {
        if (training.getAthlete() == null) {
            adding = false;
            onlineTrainingService().delete(training);
            return sendMenu(chatId);
        } else if (training.getDate() == null) {
            training.setAthlete(null);
            return sendAthletes(chatId);
        } else {
            training.setDate(null);
            return sendDates(chatId);
        }
    }

    private SendResponse cancelTraining(Long chatId, String txt) {
        choosingHelper.getTrainings().forEach(
                (s, training) -> {
                    if (s.equals(txt)) {
                        onlineTrainingService().delete(training);
                    }
                }
        );
        cancelling = false;
        return sendMenu(chatId);
    }

    private SendResponse startAdding(Long chatId) {
        athletesMarkup = backMarkup();
        for (Athlete athlete : athleteService().findAll()) {
            String athleteKey = athlete.getFirstName() + " " + athlete.getSecondName();
            choosingHelper.getAthletes().put(athleteKey, athlete);
            athletesMarkup.addRow(athleteKey);
        }
        adding = true;
        choosingHelper.setTraining(new OnlineTraining());
        return sendAthletes(chatId);
    }

    private SendResponse sendSchedule(Long chatId) {
        String info = """
                                
                %s
                                
                """;
        StringBuilder stringBuilder = new StringBuilder();
        onlineTrainingService().findAll().forEach(
                training -> stringBuilder
                        .append(String.format(info, training.getDescription()))
        );
        return sender().sendResponse(new SendMessage(chatId, stringBuilder.toString()));
    }

    private SendResponse startCancelling(Long chatId) {
        cancelling = true;
        return sendTrainings(chatId);
    }

    private SendResponse sendTrainings(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Выберите тренировку")
                .replyMarkup(trainingsMarkup()));
    }

    private SendResponse sendMenu(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Ты в блоке манипуляции тренировками")
                .replyMarkup(markup()));
    }

    private SendResponse sendDates(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Следующим сообщением выбери дату")
                .replyMarkup(datesMarkup()));
    }

    private SendResponse sendTimes(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Выбери время")
                .replyMarkup(timesMarkup()));
    }

    private SendResponse sendAthletes(Long chatId) {
        return sender().sendResponse(new SendMessage(chatId, "Выбери атлета")
                .replyMarkup(athletesMarkup));
    }

    private ReplyKeyboardMarkup trainingsMarkup() {
        ReplyKeyboardMarkup trainingsMarkup = backMarkup();
        onlineTrainingService().findAll().forEach(
                training -> {
                    if (!training.isDone()) {
                        String trainingKey = training.getDescription();
                        choosingHelper.trainings.put(trainingKey, training);
                        trainingsMarkup.addRow(trainingKey);
                    }
                }
        );
        return trainingsMarkup;
    }

    private ReplyKeyboardMarkup datesMarkup() {
        ReplyKeyboardMarkup datesMarkup = backMarkup();
        LocalDate date = LocalDate.now();
        LocalDate criticalDate = date.plusDays(14);
        while (date.isBefore(criticalDate)) {
            datesMarkup.addRow(date.toString());
            date = date.plusDays(1);
        }
        return datesMarkup;
    }

    private ReplyKeyboardMarkup timesMarkup() {
        ReplyKeyboardMarkup timesMarkup = backMarkup();
        List<OnlineTraining> trainings = onlineTrainingService().findAll();
        LocalTime time = LocalTime.of(9, 0, 0);
        LocalTime criticalTime = time.plusHours(12);
        LocalDate keptDate = choosingHelper.getTraining().getDate();
        if (trainings.stream().noneMatch(training ->
                training.getDate().equals(keptDate))) {
            while (time.isBefore(criticalTime)) {
                timesMarkup.addRow(time.toString());
                time = time.plusMinutes(30);
            }
        } else {
            List<LocalTime> times = new ArrayList<>();
            trainings.stream().filter(training -> training.getDate().equals(keptDate)).toList().forEach(training -> times.add(training.getTime()));
            while (time.isBefore(criticalTime)) {
                if (!times.contains(time) && !times.contains(time.plusMinutes(30)) && !times.contains(time.minusMinutes(30))) {
                    timesMarkup.addRow(time.toString());
                }
                time = time.plusMinutes(30);
            }
        }
        return timesMarkup;
    }

    protected List<String> buttons() {
        return List.of(
                "Записать на тренировку",
                "Отменить тренировку",
                "Посмотреть общее расписание"
        );
    }
}

