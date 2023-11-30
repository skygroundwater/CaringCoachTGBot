package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.TrainerHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.models.OnlineTraining;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TrainingPlanInterface extends AccountInterface<TrainingPlanInterface.TrainingPlanHelper> {

    public TrainingPlanInterface(TelegramSender sender, ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    @Setter
    @Getter
    public static class TrainingPlanHelper extends TrainerHelper {

        public TrainingPlanHelper(Trainer trainer) {
            super(trainer);
        }

        private final Map<String, Athlete> athletes = new HashMap<>();

        private final Map<String, OnlineTraining> trainings = new HashMap<>();

        private Athlete athlete;

        private OnlineTraining training;

        private ReplyKeyboardMarkup athletesMarkup;

        private ReplyKeyboardMarkup trainingsMarkup;

        private boolean makingTrainingPlan;

        private boolean adding;

        private boolean cancelling;

        private boolean date;

        private boolean time;

    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new TrainingPlanHelper(trainerService().findTrainerById(chatId)));
        return msg(chatId, "Вы в блоке памятки по питанию", markup());
    }

    @Override
    public List<String> buttons() {
        return List.of(
                "Записать на тренировку",
                "Отменить тренировку",
                "Посмотреть общее расписание"
        );
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, TrainingPlanHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Записать на тренировку" -> {
                return startAdding(chatId, helper);
            }
            case "Отменить тренировку" -> {
                return startCancelling(chatId, helper);
            }
            case "Посмотреть общее расписание" -> {
                return sendSchedule(chatId, helper);
            }
            case "Назад" -> {
                return goToBack(chatId);
            }
        }
        throw new NotValidDataException();
    }

    @Override
    protected SendResponse work(Long chatId, Message message, TrainingPlanHelper helper) {
        String txt = message.text();
        if (helper.adding) {
            return fillOnlineTraining(chatId, txt, helper);
        } else if (helper.cancelling) {
            return cancelTraining(chatId, txt, helper);
        } else return sendMenu(chatId);
    }

    private SendResponse fillOnlineTraining(Long chatId, String txt, TrainingPlanHelper helper) {
        OnlineTraining onlineTraining = helper.getTraining();
        if (txt.equals("Назад")) {
            return goBack(chatId, onlineTraining, helper);
        }
        if (onlineTraining.getAthlete() == null) {
            Athlete athlete = helper.getAthletes().get(txt);
            if (athlete != null) {
                onlineTraining.setAthlete(athlete);
                onlineTraining.setDescription(athlete.getFirstName() + " " + athlete.getSecondName());
            }
            return sendDates(chatId);
        } else if (onlineTraining.getDate() == null) {
            onlineTraining.setDate(LocalDate.parse(txt));
            onlineTraining.setDescription(onlineTraining.getDescription() + " " + txt);
            return sendTimes(chatId, helper);
        } else {
            onlineTraining.setDescription(onlineTraining.getDescription() + " " + txt);
            onlineTraining.setTime(LocalTime.parse(txt));
            onlineTrainingService().post(onlineTraining);
            helper.adding = false;
            msg(chatId, "Вы записали на тренировку атлета " + onlineTraining.getAthlete().getFirstName());
            msg(onlineTraining.getAthlete().getId(),
                    "Вы записаны на тренировку " + onlineTraining.getDate() + " в " + onlineTraining.getTime());
            helper.getAthletes().clear();
            helper.setTraining(null);
            return sendMenu(chatId);
        }
    }

    private SendResponse goBack(Long chatId, OnlineTraining training, TrainingPlanHelper helper) {
        if (training.getAthlete() == null) {
            helper.adding = false;
            onlineTrainingService().delete(training);
            return sendMenu(chatId);
        } else if (training.getDate() == null) {
            training.setAthlete(null);
            return sendAthletes(chatId, helper);
        } else {
            training.setDate(null);
            return sendDates(chatId);
        }
    }

    private SendResponse cancelTraining(Long chatId, String txt, TrainingPlanHelper helper) {
        helper.getTrainings().forEach(
                (s, training) -> {
                    if (s.equals(txt)) {
                        onlineTrainingService().delete(training);
                    }
                }
        );
        helper.cancelling = false;
        return sendMenu(chatId);
    }

    private SendResponse startAdding(Long chatId, TrainingPlanHelper helper) {
        helper.athletesMarkup = backMarkup();
        for (Athlete athlete : athleteService().findAll()) {
            String athleteKey = athlete.getFirstName() + " " + athlete.getSecondName();
            helper.getAthletes().put(athleteKey, athlete);
            helper.athletesMarkup.addRow(athleteKey);
        }
        helper.adding = true;
        helper.setTraining(new OnlineTraining());
        return sendAthletes(chatId, helper);
    }

    private SendResponse sendSchedule(Long chatId, TrainingPlanHelper helper) {
        String info = """
                                
                %s
                                
                """;
        StringBuilder stringBuilder = new StringBuilder();
        onlineTrainingService().findAll().forEach(
                training -> stringBuilder
                        .append(String.format(info, training.getDescription()))
        );
        return msg(chatId, stringBuilder.toString());
    }

    private SendResponse startCancelling(Long chatId, TrainingPlanHelper helper) {
        helper.cancelling = true;
        return sendTrainings(chatId, helper);
    }

    private SendResponse sendTrainings(Long chatId, TrainingPlanHelper helper) {
        return msg(chatId, "Выберите тренировку", trainingsMarkup(helper));
    }

    private SendResponse sendMenu(Long chatId) {
        return msg(chatId, "Ты в блоке манипуляции тренировками", markup());
    }

    private SendResponse sendDates(Long chatId) {
        return msg(chatId, "Следующим сообщением выбери дату", datesMarkup());
    }

    private SendResponse sendTimes(Long chatId, TrainingPlanHelper helper) {
        return msg(chatId, "Выбери время", timesMarkup(helper));
    }

    private SendResponse sendAthletes(Long chatId, TrainingPlanHelper helper) {
        return msg(chatId, "Выбери атлета", helper.athletesMarkup);
    }

    private ReplyKeyboardMarkup trainingsMarkup(TrainingPlanHelper helper) {
        ReplyKeyboardMarkup trainingsMarkup = backMarkup();
        onlineTrainingService().findAll().forEach(
                training -> {
                    if (!training.isDone()) {
                        String trainingKey = training.getDescription();
                        helper.trainings.put(trainingKey, training);
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

    private ReplyKeyboardMarkup timesMarkup(TrainingPlanHelper helper) {
        ReplyKeyboardMarkup timesMarkup = backMarkup();
        List<OnlineTraining> trainings = onlineTrainingService().findAll();
        LocalTime time = LocalTime.of(9, 0, 0);
        LocalTime criticalTime = time.plusHours(12);
        LocalDate keptDate = helper.getTraining().getDate();
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
}