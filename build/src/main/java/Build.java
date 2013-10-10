import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2013-10-10 07:06
 */
public class Build {

  public static void main(String[] args) throws Exception {

    Runtime runtime = Runtime.getRuntime();

    File carto = new File("/usr/bin/carto");
    if (!carto.exists()) {
      throw new RuntimeException(carto.getAbsolutePath() + " does not exists!");
    }


    File path = new File("../hyddafull");
    if (!path.exists()) {
      throw new RuntimeException("Path " + path.getAbsolutePath() + " does not exists!");
    }

    File projectFile = new File(path, "project.mml");

    String projectString = FileUtils.readFileToString(projectFile, "utf8");

    projectString = projectString.replaceAll("tile-sweden\\.openstreetmap\\.se", "localhost");

    File fullFileMML = new File(path, "hydda_full.mml");
    File baseFileMML = new File(path, "hydda_base.mml");
    File roadsAndLabelsFileMML = new File(path, "hydda_roads_and_labels.mml");

    String fullStringMML = projectString;
    fullStringMML = fullStringMML.replace("http://mapbox-geodata.s3.amazonaws.com/natural-earth-1.3.0/physical/10m-land.zip", "/etc/mapnik-osm-data/hydda/10m-land.shp");
    fullStringMML = fullStringMML.replace("http://tilemill-data.s3.amazonaws.com/osm/coastline-good.zip", "/usr/share/mapnik-osm-data/world_boundaries/processed_p.shp");
    fullStringMML = fullStringMML.replace("http://tilemill-data.s3.amazonaws.com/osm/shoreline_300.zip", "/usr/share/mapnik-osm-data/world_boundaries/shoreline_300.shp");
    FileUtils.write(fullFileMML, fullStringMML, "utf8");

    String baseStringMML = projectString;
    baseStringMML = baseStringMML.replace("\"base.mms\",", "\"base.mms\"");
    baseStringMML = baseStringMML.replace("\"roads.mms\",", "");
    baseStringMML = baseStringMML.replace("\"labels.mms\"", "");
    FileUtils.write(baseFileMML, baseStringMML, "utf8");

    String roadsAndLabelsStringMML = projectString;
    roadsAndLabelsStringMML = roadsAndLabelsStringMML.replaceFirst("\\{", "{ \"background-color\":\"transparent\",");
    roadsAndLabelsStringMML = roadsAndLabelsStringMML.replace("\"base.mms\",", "");
    FileUtils.write(roadsAndLabelsFileMML, roadsAndLabelsStringMML, "utf8");

    exec(carto, fullFileMML, path);
    exec(carto, baseFileMML, path);
    exec(carto, roadsAndLabelsFileMML, path);

    File fullFileXML = new File(path, "hydda_full.xml");
    File baseFileXML = new File(path, "hydda_base.xml");
    File roadsAndLabelsFileXML = new File(path, "hydda_roads_and_labels.xml");

    String fullStringXML = FileUtils.readFileToString(fullFileXML, "utf8");
    String baseStringXML = FileUtils.readFileToString(baseFileXML, "utf8");
    String roadsAndLabelsStringXML = FileUtils.readFileToString(roadsAndLabelsFileXML, "utf8");

    fullStringXML = fullStringXML.replaceFirst(Pattern.quote("<Parameters>") + ".+" + Pattern.quote("</Parameters>"), "");
    baseStringXML = baseStringXML.replaceFirst(Pattern.quote("<Parameters>") + ".+" + Pattern.quote("</Parameters>"), "");
    roadsAndLabelsStringXML = roadsAndLabelsStringXML.replaceFirst(Pattern.quote("<Parameters>") + ".+" + Pattern.quote("</Parameters>"), "");

    FileUtils.write(fullFileXML, fullStringXML, "utf8");
    FileUtils.write(baseFileXML, baseStringXML, "utf8");
    FileUtils.write(roadsAndLabelsFileXML, roadsAndLabelsStringXML, "utf8");

    System.currentTimeMillis();

  }

  private static void exec(File carto, File mml, File path) throws Exception {
    System.out.println("Executing Carto on " + mml.getAbsolutePath() + " in path " + path.getAbsolutePath());
    Process process = Runtime.getRuntime().exec(new String[]{carto.getAbsolutePath(), mml.getName()}, new String[]{}, path);
    process.waitFor();
    System.out.println("Done.");
  }

}