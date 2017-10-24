package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;

    private String s3BucketName;

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {
        this.s3Client = s3Client;
        this.s3BucketName = s3BucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.contentType);
        metadata.setContentLength(blob.inputStream.available());
        s3Client.putObject(s3BucketName, blob.name, blob.inputStream, metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        if (s3Client.doesObjectExist(s3BucketName, name)) {
            S3Object s3Object = s3Client.getObject(s3BucketName, name);
            Blob blob = new Blob(name,
                    s3Object.getObjectContent(),
                    s3Object.getObjectMetadata().getContentType());
            return Optional.of(blob);
        } else {
            return Optional.empty();
        }
    }
}
