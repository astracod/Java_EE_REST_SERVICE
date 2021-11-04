package ru.avrsoft.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.avrsoft.dto.FileResponse;
import ru.avrsoft.dto.SaveFile;
import ru.avrsoft.dto.StatusCheck;
import ru.avrsoft.exception.RequestProcessingException;
import ru.avrsoft.repository.FileResourceSQLQuery;
import ru.avrsoft.repository.FileSharing;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class FileService {


    @EJB
    private FileResourceSQLQuery fileResourceSQLQuery;

    @EJB
    private FileSharing fileSharing;

    public String test() {
        return "Hello";
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

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

    /**
     * 4. GET Отдача файлового контента то есть потока InputStream по file_name или по sha5
     *
     * @param
     */
    public Response getInputStream(String fileName) {

        String filePath = fileResourceSQLQuery.getFileByNameOrHash(fileName).getFilePath();

        return fileSharing.servingFileContentToInputStream(filePath);
    }

    /**
     * 5. POST Сохранение файла, то есть это Получение файла метод upload, которая на вход получает InputStream и также task_id, report_id
     */
    public Response saveFileByID(Integer taskId, Integer reportId, InputStream dataStream) {

        if (fileSharing.retrievingAndWritingAFileToStorage(dataStream)) {

            SaveFile saveFile = fileResourceSQLQuery.saveFileById(taskId, reportId);

            return Response
                    .status(Response.Status.OK)
                    .entity(saveFile.getAnswerBase())
                    .build();
        } else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("File not written")
                    .build();
        }

    }

    /**
     * 6. @DELETE удаление  записи о файле из бд по имени файла и id User
     */
    public Response removeFileByIdAndFileName(Integer id, String fileName) {

        StatusCheck statusCheck = fileResourceSQLQuery.removeFileByIdAndFileName(id, fileName);

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

























