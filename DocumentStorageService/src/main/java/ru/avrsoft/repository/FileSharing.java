package ru.avrsoft.repository;

import ru.avrsoft.exception.RequestProcessingException;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless
public class FileSharing {


    public static final String REMOTE_REPOSITORY = "C:\\Users\\Admin\\Desktop\\savePlace.txt";


    /**
     * 4. GET Отдача файлового контента то есть потока InputStream по file_name или по sha5
     *
     * @param
     */
    public Response servingFileContentToInputStream(String filePath) {

        if (filePath == null) {
            throw new RequestProcessingException("Attention !!! The file was not found on this path.");
        }
        try (ByteArrayOutputStream ous = new ByteArrayOutputStream();) {
            String fileName1 = null;

            File f = new File(filePath);

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

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(406).build();
    }


    /**
     * 5. POST Сохранение файла, то есть это Получение файла метод upload, которая на вход получает InputStream и также task_id, report_id
     */
    public boolean retrievingAndWritingAFileToStorage(InputStream data) {

        boolean result = false;

        byte[] buffer;

        try (OutputStream ous = new FileOutputStream(new java.io.File(REMOTE_REPOSITORY))) {

            buffer = new byte[4096];
            int read = 0;
            while ((read = data.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
            ous.flush();
            ous.close();
            result = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * запись в файл
     *
     * @param dataStream
     */
    private void writeTiFile(String dataStream) {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateCreation = formatter.format(date);

        try (FileWriter writer = new FileWriter(dataStream, false)) {

            String text = "Запись произведена : " + dateCreation;
            writer.write(text);
            writer.append(" ПРОБА!");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
