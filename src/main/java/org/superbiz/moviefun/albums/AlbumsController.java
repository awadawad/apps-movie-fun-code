package org.superbiz.moviefun.albums;

import com.amazonaws.util.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;

    private final BlobStore blobStore;

    @Autowired
    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {

        Blob blob = new Blob(getCoverName(albumId),
                uploadedFile.getInputStream(),
                uploadedFile.getContentType());
        blobStore.put(blob);
        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {

        Optional<Blob> optional = blobStore.get(getCoverName(albumId));
        Blob blob;
        if (optional.isPresent()) {

            blob = optional.get();
        } else {

            URL coverUrl = this.getClass().getClassLoader().getResource("default-cover.jpg");
            blob = new Blob(getCoverName(albumId), coverUrl.openStream(), new Tika().detect(coverUrl));
        }

        byte[] buffer = IOUtils.toByteArray(blob.inputStream);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType(blob.contentType));
        headers.setContentLength(buffer.length);
        return new HttpEntity<>(buffer, headers);
    }

    private String getCoverName(long albumId) {
        return format("covers/%d", albumId);
    }
}
