package com.caringcoachtelegrambot.blocks.secondary.accounts.athleteinterfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers.AthleteHelper;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Component
public class EditingInterfaceAthlete extends AccountInterface<EditingInterfaceAthlete.EditingHelper> {

    public EditingInterfaceAthlete(TelegramSender sender,
                                   ServiceKeeper serviceKeeper) {
        super(sender, serviceKeeper);
    }

    public static class EditingHelper extends AthleteHelper {
        public EditingHelper(Athlete athlete) {
            super(athlete);
        }

        private String newPass;
        private boolean login;
        private boolean waitingOldPassword;
        private boolean confirmedPassword;
        private boolean newPassword;
        private boolean name;
        private boolean secondName;
        private boolean age;
        private boolean height;
        private boolean weight;
    }

    @Override
    public SendResponse uniqueStartBlockMessage(Long chatId) {
        signIn(chatId, new EditingHelper(athleteService().findById(chatId)));
        return msg(chatId, "Раздел редактирования", markup());
    }

    @Override
    public List<String> buttons() {
        return List.of("Изменить логин",
                "Изменить пароль",
                "Изменить имя",
                "Изменить фамилию",
                "Изменить рост",
                "Изменить вес",
                "Изменить возраст");
    }

    @Override
    protected SendResponse switching(Long chatId, Message message, EditingHelper helper) {
        String txt = message.text();
        switch (txt) {
            case "Назад" -> {
                return goToBack(chatId);
            }
            case "Изменить логин" -> {
                return setLogin(chatId, helper);
            }
            case "Изменить пароль" -> {
                return setPassword(chatId, helper);
            }
            case "Изменить имя" -> {
                return setName(chatId, helper);
            }
            case "Изменить фамилию" -> {
                return setSecondName(chatId, helper);
            }
            case "Изменить рост" -> {
                return setHeight(chatId, helper);
            }
            case "Изменить вес" -> {
                return setWeight(chatId, helper);
            }
            case "Изменить возраст" -> {
                return setAge(chatId, helper);
            }
            case "Изменить фото профиля" -> {
                return msg(chatId, "Функционал пока не реализовал");
            }
        }
        throw new NotValidDataException();
    }

    private SendResponse setName(Long chatId, EditingHelper helper) {
        helper.name = true;
        helper.setWorking(true);
        return msg(chatId, "Введите новое имя", backMarkup());
    }

    private SendResponse setSecondName(Long chatId, EditingHelper helper) {
        helper.secondName = true;
        helper.setWorking(true);
        return msg(chatId, "Введите новую фамилию", backMarkup());
    }

    private SendResponse setHeight(Long chatId, EditingHelper helper) {
        helper.height = true;
        helper.setWorking(true);
        return msg(chatId, "Введите ваш рост", backMarkup());
    }

    private SendResponse setWeight(Long chatId, EditingHelper helper) {
        helper.weight = true;
        helper.setWorking(true);
        return msg(chatId, "Введите ваш вес", backMarkup());
    }

    private SendResponse setAge(Long chatId, EditingHelper helper) {
        helper.age = true;
        helper.setWorking(true);
        return msg(chatId, "Введите ваш возраст", backMarkup());
    }

    private SendResponse setPassword(Long chatId, EditingHelper helper) {
        helper.waitingOldPassword = true;
        helper.setWorking(true);
        return msg(chatId, "Введите ваш старый пароль", backMarkup());
    }

    private SendResponse confirmOldPass(Long chatId, String oldPass, EditingHelper helper) {
        if (trainerService().getEncoder().matches(oldPass, helper.getAthlete().getPassword())) {
            helper.confirmedPassword = true;
            return msg(chatId, "Введите ваш новый пароль", backMarkup());
        } else return msg(chatId, "Вы неверно ввели старый пароль. Попробуйте снова", backMarkup());
    }

    private SendResponse setLogin(Long chatId, EditingHelper helper) {
        helper.login = true;
        helper.setWorking(true);
        return msg(chatId, "Введите новый логин", backMarkup());
    }

    @Override
    protected SendResponse work(Long chatId, Message message, EditingHelper helper) {
        Athlete athlete = helper.getAthlete();
        String value = message.text();
        if (value.equals(BACK)) {
            return forcedStop(chatId);
        } else if (helper.login) {
            athlete.setLogin(value);
        } else if (helper.waitingOldPassword && !helper.confirmedPassword && !helper.newPassword) {
            return confirmOldPass(chatId, value, helper);
        } else if (helper.waitingOldPassword && helper.confirmedPassword && !helper.newPassword) {
            return keepNewPass(chatId, value, helper);
        } else if (helper.waitingOldPassword && helper.confirmedPassword && helper.newPassword) {
            if (helper.newPass.equals(value)) {
                athlete.setPassword(trainerService().getEncoder().encode(value));
            } else return msg(chatId, "Вы не смогли подтвердить пароль. Попробуйте снова", backMarkup());
        } else if (helper.name) {
            athlete.setFirstName(value);
        } else if (helper.secondName) {
            athlete.setSecondName(value);
        } else if (helper.height) {
            athlete.setHeight(value);
        } else if (helper.weight) {
            athlete.setWeight(value);
        } else if (helper.age) {
            athlete.setAge(value);
        }
        msg(chatId, "Вы успешно изменили данные");
        return uniqueStartBlockMessage(chatId);
    }

    private SendResponse keepNewPass(Long chatId, String newPass, EditingHelper helper) {
        helper.newPass = newPass;
        helper.newPassword = true;
        return msg(chatId, "Повторно введите новый пароль", backMarkup());
    }
}