package ru.avrsoft.service;

import ru.avrsoft.dto.FileResponse;
import ru.avrsoft.dto.SaveFile;
import ru.avrsoft.dto.StatusCheck;
import ru.avrsoft.exception.RequestProcessingException;
import ru.avrsoft.repository.FileResourceSQLQuery;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class FileService {

    @EJB
    private FileResourceSQLQuery fileResourceSQLQuery;

    public String test() {
        return "Hello";
    }


    /**
     * API методы
     * 1. GET Получение списка файлов для пользователя
     * метод вернет данные:
     * file_name
     * file_path
     * sha5
     *
     * @param workerId
     * @return
     */
    public List<FileResponse> getAllFilesByWorkerId(Integer workerId) {
        List<FileResponse> fileResponsesFromTaskId = null;
        List<FileResponse> fileResponsesFromReportId = null;
        Integer checkList = fileResourceSQLQuery.getTaskIdAndReportIdByWorkerId(workerId).get(0);
        if (checkList > 0) {
            fileResponsesFromTaskId = fileResourceSQLQuery.getListFilesByTaskId(fileResourceSQLQuery.getTaskIdAndReportIdByWorkerId(workerId).get(0), 2);
            fileResponsesFromReportId = fileResourceSQLQuery.getListFilesByReportId(fileResourceSQLQuery.getTaskIdAndReportIdByWorkerId(workerId).get(1), 2);
        } else if (checkList < 0) {
            throw new RequestProcessingException(" ATTENTION : File with this id does not exist");
        }
        return Stream.concat(fileResponsesFromReportId.stream(), fileResponsesFromTaskId.stream()).distinct().collect(Collectors.toList());
    }


    /**
     * 2. GET Получение списка файлов для одного отчета по report_id
     * Отдает: id, file_name
     * static
     *
     * @param reportId
     * @return
     */
    public List<FileResponse> getListFilesByReportId(Integer reportId) {
        return fileResourceSQLQuery.getListFilesByReportId(reportId, 0);
    }


    /**
     * 3. GET Получение списка файлов для одной задачи по task_id
     * Отдает: id, file_name
     *
     * @param taskId
     * @return
     */
    public List<FileResponse> getListFilesByTaskId(Integer taskId) {
        return fileResourceSQLQuery.getListFilesByTaskId(taskId, 0);
    }

    public Response getInputStream(String fileName) {

        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        String fileName1 = null;
        File f = null;

        try {
            String filePath = FileResourceSQLQuery.getFileByNameOrHash(fileName).getFilePath();

            if (filePath != null) {
                f = new File(filePath);
            }
            InputStream ios = null;
            byte[] buffer = new byte[4096];
            ios = new FileInputStream(f);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
            return Response.ok(
                    (InputStream) (new ByteArrayInputStream(ous.toByteArray())))
                    .header("fileName", fileName1)
                    .build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Response.status(406).build();
    }

    public Response saveFileByID(Integer taskId, Integer reportId, InputStream dataStream) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateCreation = formatter.format(date);

        SaveFile saveFile = FileResourceSQLQuery.saveFileById(dataStream, taskId, reportId);

        return Response
                .status(Response.Status.OK)
                .entity(saveFile.getAnswerBase())
                .build();
    }

    public Response removeFileByIdAndFileName(Integer id, String fileName) {

        StatusCheck statusCheck = FileResourceSQLQuery.removeFileByIdAndFileName(id, fileName);

        if (statusCheck.getResultQuery() == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(statusCheck.getStatusCheck())
                    .build();
        } else {
            return Response
                    .status(Response.Status.OK)
                    .entity(statusCheck.getStatusCheck())
                    .build();
        }

    }

}

























