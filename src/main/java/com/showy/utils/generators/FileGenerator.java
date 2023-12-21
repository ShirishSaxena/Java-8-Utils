package com.showy.utils.generators;

import com.showy.utils.constant.FileTypes;
import com.showy.utils.model.CustomFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FileGenerator {



    public static CustomFile generate(List<Object[]> rowDataList, FileTypes fileType, String fileName) {
        /** Generate Csv & upload to sftp **/
        try {
            CustomFile customFile = null;
            switch (fileType) {
                case CSV:
                    customFile = CsvGenerator.writeCsv(rowDataList);
                    break;
                case XLSX:
                    customFile = ExcelGenerator.writeExcel(rowDataList);
                    break;
                default:
                    return new CustomFile();
            }
            customFile.setFileName(fileName);
            return customFile;
        } catch (Exception e) {
            log.error("Error in file generator : " + fileName);
            return new CustomFile();
        }
    }
}
