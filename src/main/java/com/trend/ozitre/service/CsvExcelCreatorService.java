package com.trend.ozitre.service;

import com.trend.ozitre.entity.EventsEntity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface CsvExcelCreatorService {

    void createPaymentCsv(List<EventsEntity> eventsEntities);

    ByteArrayOutputStream createPaymentExcel(List<EventsEntity> eventsEntities, Long studentId) throws IOException;
}
