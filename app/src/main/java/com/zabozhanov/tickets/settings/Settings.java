package com.zabozhanov.tickets.settings;

/**
 * Created by z0rgoyok on 18.05.16.
 * Сохрарение и получения настроек
 */
public class Settings {

    /**
     * Возвращает необходимую длину пакета для отправки
     * @return
     */
    public long packageLength() {
        return 10000;
    }

    /**
     * Идентификатор устройства в сети
     * @return
     */
    public byte devicID() {
        return 1;
    }


}
