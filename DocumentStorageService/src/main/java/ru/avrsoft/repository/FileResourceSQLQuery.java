package ru.avrsoft.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.avrsoft.dto.FileResponse;
import ru.avrsoft.dto.SaveFile;
import ru.avrsoft.dto.StatusCheck;
import ru.avrsoft.entities.File;
import ru.avrsoft.exception.RequestProcessingException;

import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class FileResourceSQLQuery {

    public static final String GET_TASKID_AND_REPORTID_BY_WORKERID = "select task.id, task.report_id from \"public\".task where task.worker_id = ?";

    public static final String GET_LIST_FILES_BY_REPORTID = "SELECT f.id, f.file_name FROM  \"public\".report inner join files f on report.id = f.report_id where report.id = ?";

    public static final String GET_LIST_FILES_BY_REPORTID_V3 = "SELECT f.id, f.file_name, f.sha5 FROM  \"public\".report inner join files f on report.id = f.report_id where report.id = ?";

    public static final String GET_LIST_FILES_BY_TASKID = "SELECT f.id, f.file_name FROM  \"public\".task inner join files f on task.id = f.task_id where task.id = ?";

    public static final String GET_LIST_FILES_BY_TASKID_V3 = "SELECT f.id, f.file_name, f.sha5 FROM  \"public\".task inner join files f on task.id = f.task_id where task.id = ?";

    public static final String GET_FILE_BY_NAME_OR_HASH = "select * from \"public\".files where files.sha5 = ? or files.file_name = ?";

    public static final String SAVE_FILE_BY_TASK_ID = "insert into \"public\".files (file_name, file_path, sha5,  task_id) values( ?, ?, ?, ?)";

    public static final String SAVE_FILE_BY_REPORT_ID = "insert into \"public\".files (file_name, file_path, sha5, report_id, task_id) values( ?, ?, ?, ?)";

    public static final String DELETE_FROM_FILES_BY_ID_AND_FILE_NAME = "delete from \"public\".files where  file_name=?";

    public static final String GET_WORKER_ID = "select worker_id from \"public\".task left join files f on task.id = f.task_id where file_name=?";

    public static final String FILE_NAME_SEARCH = "select * from \"public\".files where files.file_name = ?";

    public static final String TRAINING_PHYSICAL_FILES_STORAGE = "C:\\Users\\Admin\\Desktop\\";

    private static DataSource dataSource = ConnectionDB.INSTANCE.getDataSource();

    public static final Logger LOGGER = LoggerFactory.getLogger(FileResourceSQLQuery.class);


    /**
     * 2. GET Получение списка файлов для одного отчета по report_id
     * Отдает: id, file_name
     *
     * @param reportId
     * @return
     */
    public List<FileResponse> getListFilesByReportId(Integer reportId, Integer status) {
        return getListFiles(reportId, status, GET_LIST_FILES_BY_REPORTID, GET_LIST_FILES_BY_REPORTID_V3);
    }

    /**
     * 3. GET Получение списка файлов для одной задачи по task_id
     * Отдает: id, file_name
     *
     * @param taskId
     * @return
     */
    public List<FileResponse> getListFilesByTaskId(Integer taskId, Integer status) {
        return getListFiles(taskId, status, GET_LIST_FILES_BY_TASKID, GET_LIST_FILES_BY_TASKID_V3);
    }

    /**
     * 4. GET Отдача файлового контента то есть потока InputStream по file_name или по sha5
     *
     * @param
     */
    public static FileResponse getFileByNameOrHash(String dataFile) {

        FileResponse fileResponse = new FileResponse();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_FILE_BY_NAME_OR_HASH);
            statement.setString(1, dataFile);
            statement.setString(2, dataFile);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.wasNull()) {
                fileResponse = mapperFileForInput(resultSetToListFilesForInput(resultSet));
            } else {
                throw new RequestProcessingException("File with this id does not exist");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileResponse;
    }

    /**
     * 5. POST Сохранение файла, то есть это Получение файла метод upload, которая на вход получает InputStream и также task_id, report_id
     */
    public static SaveFile saveFileById(InputStream dataFile, Integer taskId, Integer reportId) {
        SaveFile saveFile = new SaveFile();


        String fullFilePath = "C:\\Users\\Admin\\Desktop\\savePlace.txt"; // указываю место куда сохраняю
        String[] address = fullFilePath.split("\\\\"); // надо заменить на используемый путь
        String filePath = address[4].substring(0, address[4].indexOf(".")); // формирование названия файла

        boolean fileNameComparisonResult = getFileByFileName(filePath);

        byte[] buffer;
        OutputStream ous = null; // стрим в который будет сохранятся фаил , пришедший из инпут срима
        try {
            ous = new FileOutputStream(new java.io.File(fullFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            buffer = new byte[4096];
            int read = 0;
            while ((read = dataFile.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
            ous.flush();
            ous.close();
            if (!fileNameComparisonResult) {
                StatusCheck statusCheck = putFileInDataBase(filePath, fullFilePath, taskId, reportId);

                int result = statusCheck.getResultQuery();
                if (result > 0) {
                    saveFile.setAnswerBase("success");
                } else {
                    saveFile.setAnswerBase("fail");
                    saveFile.setFilePath(filePath);
                    saveFile.setFullFilePath(fullFilePath);
                    saveFile.setTaskId(taskId);
                    saveFile.setReportId(reportId);
                }
            } else {
                saveFile.setAnswerBase("A file with the same name already exists, its data has been updated");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveFile;
    }


    /**
     * 6. @DELETE удаление  записи о файле из бд по имени файла и id User
     */
    public static StatusCheck removeFileByIdAndFileName(Integer id, String fileName) {

        boolean statusStorage = false;
        Integer workerId = 0;
        Integer result = 0;

        StatusCheck statusCheck = new StatusCheck();

        if (id == null || fileName == null) {
            throw new RequestProcessingException(" ATTENTION : Be careful when filling in the fields. Input data cannot be empty.");
        } else {
            workerId = getWorkerIdFromTask(fileName);

            if (workerId == id) {

                try (Connection connection = dataSource.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_FILES_BY_ID_AND_FILE_NAME);
                    preparedStatement.setString(1, fileName);
                    result = preparedStatement.executeUpdate();

                    if (result > 0) {
                        statusCheck.setResultQuery(result);
                        statusCheck.setStatusCheck("successful");
                    } else {
                        throw new RequestProcessingException(" ATTENTION : No file found for this request.");
                    }
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }

                statusStorage = deletingFileFromPhysicalStorage(fileName);

                if (!statusStorage) {

                    throw new RequestProcessingException(" ATTENTION : Physical file not found.");
                }

            }
        }

        return statusCheck;
    }

    private static boolean getFileByFileName(String fileName) {
        String name = "";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FILE_NAME_SEARCH);
            statement.setString(1, fileName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("file_name");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name.equals(fileName);
    }

    private static Integer getWorkerIdFromTask(String fileName) {
        Integer result = 0;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_WORKER_ID);
            preparedStatement.setString(1, fileName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("worker_id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    private static boolean deletingFileFromPhysicalStorage(String fileName) {
        String fullFilePath = TRAINING_PHYSICAL_FILES_STORAGE + fileName + ".txt";
        java.io.File file = new java.io.File(fullFilePath);
        return file.delete();
    }


    private static StatusCheck putFileInDataBase(String fileName, String fullFilePath, Integer taskId, Integer reportId) {
        StatusCheck statusCheck = new StatusCheck();

        String hash = Integer.toHexString(fileName.hashCode());
        Integer result = 0;

        try (Connection connection = dataSource.getConnection();) {
            if (taskId != null && reportId == null) {
                PreparedStatement statement = connection.prepareStatement(SAVE_FILE_BY_TASK_ID);
                statement.setString(1, fileName);
                statement.setString(2, fullFilePath);
                statement.setString(3, hash);
                statement.setInt(4, taskId);
                result = statement.executeUpdate();
                statusCheck.setResultQuery(result);
            } else if (reportId != null && taskId == null) {
                PreparedStatement statement = connection.prepareStatement(SAVE_FILE_BY_REPORT_ID);
                statement.setString(1, fileName);
                statement.setString(2, fullFilePath);
                statement.setString(3, hash);
                statement.setInt(4, reportId);
                result = statement.executeUpdate();
                statusCheck.setResultQuery(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusCheck;
    }

    private static List<FileResponse> getListFiles(Integer reportOrTaskId, Integer status, String statusOne, String statusTwo) {

        List<FileResponse> fileResponses = null;
        try (Connection connection = dataSource.getConnection()) {

            if (status == 0) {

                ResultSet resultSet = getResultSet(statusOne, reportOrTaskId, connection);
                fileResponses = mapperFile(resultSetToListFiles(resultSet));

            } else if (status == 2) {

                ResultSet resultSet = getResultSet(statusTwo, reportOrTaskId, connection);
                fileResponses = mapperFile(resultSetToListFilesV3(resultSet));

            }

            return fileResponses;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileResponses;
    }

    private static List<FileResponse> mapperFile(List<File> files) {
        List<FileResponse> fileResponses = new ArrayList<>(files.size());
        for (File file : files) {
            fileResponses.add(mapperFileForInput(file));
        }
        return fileResponses;
    }

    private static List<File> resultSetToListFiles(ResultSet resultSet) throws SQLException {
        List<File> files = new ArrayList<>(resultSet.getFetchSize());
        while (resultSet.next()) {
            File file = new File();
            file.setId(resultSet.getInt("id"));
            file.setFileName(resultSet.getString("file_name"));
            files.add(file);
        }

        return files;
    }

    private static FileResponse mapperFileForInput(File files) {
        FileResponse fileResponses = new FileResponse();
        fileResponses.setId(files.getId());
        fileResponses.setFileName(files.getFileName());
        fileResponses.setFilePath(files.getFilePath());
        fileResponses.setSha5(files.getSha5());
        fileResponses.setReportId(files.getReportId());
        fileResponses.setTaskId(files.getTaskId());
        return fileResponses;
    }

    private static File resultSetToListFilesForInput(ResultSet resultSet) throws SQLException {
        File file = new File();
        while (resultSet.next()) {
            file.setId(resultSet.getInt("id"));
            file.setFileName(resultSet.getString("file_name"));
            file.setFilePath(resultSet.getString("file_path"));
            file.setSha5(resultSet.getString("sha5"));
            file.setReportId(resultSet.getInt("report_id"));
            file.setTaskId(resultSet.getInt("task_id"));
        }
        return file;
    }

    private static List<File> resultSetToListFilesV3(ResultSet resultSet) throws SQLException {

        List<File> files = new ArrayList<>();
        while (resultSet.next()) {
            File file = new File();
            file.setId(resultSet.getInt("id"));
            file.setFileName(resultSet.getString("file_name"));
            file.setSha5(resultSet.getString("sha5"));
            files.add(file);
        }
        return files;
    }

    private static ResultSet getResultSet(String sqlString, Integer reportId, Connection connection) throws SQLException {

        PreparedStatement statement1 = connection.prepareStatement(sqlString);
        statement1.setInt(1, reportId);
        return statement1.executeQuery();
    }

    public List<Integer> getTaskIdAndReportIdByWorkerId(Integer workerId) {
        List<Integer> listId = Collections.synchronizedList(new ArrayList<>());
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_TASKID_AND_REPORTID_BY_WORKERID,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            statement.setInt(1, workerId);
            ResultSet resultSet = statement.executeQuery();


            int numberOfRows = 0;
            resultSet.last();
            numberOfRows = resultSet.getRow();

            if (numberOfRows == 0) {
                listId.add(-1);
            }

            if (numberOfRows > 0) {
                resultSet.beforeFirst();
            }

            if (numberOfRows > 0) {
                while (resultSet.next()) {
                    listId.add(resultSet.getInt("id"));
                    listId.add(resultSet.getInt("report_id"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listId;
    }


}
