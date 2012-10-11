package integrity_api;
import java.util.*;
public interface IIntegrityApi {
  Collection<Map<String,String>> searchAll(Map integrity,String datasetName);

  Collection<Map<String,String>> search(Map integrity, String datasetName, Map<String,Collection<String>> qualifiers);

  Map login(String hostName,String userName,String password);

  Collection<String> availableDatasets(Map integrity);

  Map<String,Collection<String>> availableQualifiers(Map integrity, String datasetName);
}
