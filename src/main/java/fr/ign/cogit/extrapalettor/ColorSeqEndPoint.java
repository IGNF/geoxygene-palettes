package fr.ign.cogit.extrapalettor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/build/sequence")
public class ColorSeqEndPoint {

  @GET
  @Produces(MediaType.APPLICATION_XML)
  public ColorSequence getXML(@QueryParam("n") String n) {
     return new ColorSequence(5);
//    ColorSequenceBuilder builder = new ColorSequenceBuilder();
//    return builder.buildColorSequence(Integer.parseInt(n));
  }
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ColorSequence getJSON(@QueryParam("n") String n) {
    //return new ColorSequence(5);
    return new ColorSequenceBuilder().buildColorSequence(Integer.parseInt(n));
  }

  
  
}
