package java_integrity_api;
import java.util.*;
public interface IIntegrityApi {
  List<Map<String,String>> searchAll(Map integrity,String datasetName);
  List<Map<String,String>> search(Map integrity, String datasetName, Map<String,List<String>> qualifiers);
  Map login(String hostName,String userName,String password);
}
