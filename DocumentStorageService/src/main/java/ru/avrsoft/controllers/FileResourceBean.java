package ru.avrsoft.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.avrsoft.dto.AllFilesResponse;
import ru.avrsoft.exception.RequestProcessingException;
import ru.avrsoft.service.FileService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;


@Path("attached")
@Stateless
public class FileResourceBean {

    @EJB
    private FileService fileService;

    public static final Logger LOGGER = LoggerFactory.getLogger(FileResourceBean.class);


    @GET
    @Path("get/test")
    public Response test() {
        String mes = fileService.test();
        return Response
                .status(Response.Status.OK)
                .entity(mes)
                .build();
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
    @GET
    @Path("get/worker/{workerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AllFilesResponse getAllFilesByWorkerId(@PathParam("workerId") Integer workerId) {
        AllFilesResponse allFilesResponse = new AllFilesResponse();
        allFilesResponse.setFiles(fileService.getAllFilesByWorkerId(workerId));
        return allFilesResponse;
    }

    /**
     * 2. GET Получение списка файлов для одного отчета по report_id
     * Отдает: id, file_name
     *
     * @param reportId
     * @return
     */
    @GET
    @Path("get/report/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AllFilesResponse getListFilesByReportId(@PathParam("reportId") Integer reportId) {
        AllFilesResponse allFilesResponse = new AllFilesResponse();
        if (reportId == 0) {
            throw new RequestProcessingException("Enter id number");
        } else {
            allFilesResponse.setFiles(fileService.getListFilesByReportId(reportId));
            return allFilesResponse;
        }
    }

    /**
     * 3. GET Получение списка файлов для одной задачи по task_id
     * Отдает: id, file_name
     *
     * @param taskId
     * @return
     */
    @GET
    @Path("get/task/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AllFilesResponse getListFilesByTaskId(@PathParam("taskId") Integer taskId) {
        AllFilesResponse allFilesResponse = new AllFilesResponse();
        if (taskId == 0) {
            throw new RequestProcessingException("Enter id number");
        } else {
            allFilesResponse.setFiles( fileService.getListFilesByTaskId(taskId));
            return allFilesResponse;
        }
    }

    /**
     * 4. GET Отдача файлового контента то есть потока InputStream по file_name или по sha5
     *
     * @param
     */
    @GET
    @Path("get/dm/{fileName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInputStream(@PathParam("fileName") String fileName) {
        return fileService.getInputStream(fileName);
    }


    /**
     * 5. POST Сохранение файла, то есть это Получение файла метод upload, которая на вход получает InputStream и также task_id, report_id
     */
    @POST
    @Path("myload")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveFileByID(
            @QueryParam("taskId") Integer taskId,
            @QueryParam("reportId") Integer reportId,
            InputStream dataStream
    ) {
        return fileService.saveFileByID(taskId, reportId, dataStream);
    }

    /**
     * 6. @DELETE удаление  записи о файле из бд по имени файла и id User
     */
    @DELETE
    @Path("removefile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFileByIdAndFileName(
            @QueryParam("id") Integer id,
            @QueryParam("fileName") String fileName) {
        return fileService.removeFileByIdAndFileName(id, fileName);
    }

}
