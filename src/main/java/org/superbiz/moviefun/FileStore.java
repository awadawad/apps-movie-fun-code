package org.superbiz.moviefun;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[blob.inputStream.available()];
            blob.inputStream.read(buffer);
            outputStream.write(buffer);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        File coverFile = new File(name);
        if (coverFile.exists()) {

            Blob blob = new Blob(name,
                    new FileInputStream(coverFile),
                    new Tika().detect(coverFile));
            return Optional.of(blob);
        } else {

            return Optional.empty();
        }
    }


}
