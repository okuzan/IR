import java.io.File;

public class Description {
    private TitleInfo titleInfo;
    private DocumentInfo documentInfo;
    private PublishInfo publishInfo;

    public Description(File f){
        titleInfo = new TitleInfo(f);
        documentInfo = new DocumentInfo(f);
        publishInfo = new PublishInfo(f);
    }

    public TitleInfo getTitleInfo() {
        return titleInfo;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public PublishInfo getPubishInfo() {
        return publishInfo;
    }
}
