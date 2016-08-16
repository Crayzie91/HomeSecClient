package HomeSec;

import javax.jws.WebService;

/**
 * Java Interface defines a RESTful Web Service. 
 * 
 * @author khaves
 */
@WebService
public interface HomeSecClient {
    public String takePicture(String ImgDir);
    public boolean sendPicture(String ip, String path);
    public void stopSvc ();
}
