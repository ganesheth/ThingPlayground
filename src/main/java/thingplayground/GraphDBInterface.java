package thingplayground;

import java.sql.Date;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.naming.directory.InvalidAttributesException;

import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import thingplayground.models.AbstractEntity;


public class GraphDBInterface {
	
	private static Logger logger =     LoggerFactory.getLogger(StorageInterface.class);
	private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");
	private static String GRAPHDB_SERVER =   "http://localhost:7200/";
	private static String REPOSITORY_ID = "ThingData";
	private static String strInsertThing;
	private static String strDeleteEvent;
	private static String strQuery;
	RepositoryConnection repositoryConnection = null;
	ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	
	public void close() {
		if(repositoryConnection != null && repositoryConnection.isOpen()) {
			repositoryConnection.close();
		}
	}
	
	public void create(AbstractEntity entity) {
	    try {
	    		JsonNode node = objectMapper.valueToTree(entity);
		      String statement = strInsertThing.replaceAll("%id", entity.getId());
		      String triples = jsonToRdfTriples(node, entity.getId());
		      statement = statement.replace("%triples", triples);
		      executeStatement(getRepositoryConnection(), statement);
		      //query(repositoryConnection);
		    } catch (Throwable t) {
		      logger.error(WTF_MARKER, t.getMessage(), t);
		    } finally {
		      //repositoryConnection.close();
		    }		
	}
	
	public void read() {
		
	}	

	public void delete(String id) {
	    try {   
	      executeStatement(getRepositoryConnection(), strDeleteEvent.replace("%id", id));     
	    } catch (Throwable t) {
	      logger.error(WTF_MARKER, t.getMessage(), t);
	    } finally {

	    } 		
	}
	
	public void update(AbstractEntity entity) {
		delete(entity.getId());
		create(entity);
	}
	
	

	
    //{"type": "alarm","endpoint":"urn:system1","version":"1.0","timestamp":"2019-10-14T11:47:27",
    //"message":"High temperature","category":"Maintenance","priority":0,"state":"created",
    //"createdAt":"2019-10-14T11:47:27","source":"urn:analog_input_01","id":"883574"}

	
	private String jsonToRdfTriples(@Nonnull JsonNode node, @Nonnull String id) throws InvalidAttributesException {
		if(id==null || node == null) {
			throw new InvalidAttributesException();
		}
		String triples = "";
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.convertValue(node, new TypeReference<Map<String, Object>>(){});
		for(String k : map.keySet()) {
			Object val = map.get(k);
			if(val == null) {
				continue;
			}
			//"<urn:%id> btea:id \"%id\"^^xsd:string ."
			JsonNode childNode = node.findValue(k);
			if(k.compareTo("type") == 0) {
				triples += String.format("<%s> a %s .\n", id,  val);
				continue;
			}
			if(k.compareTo("id") == 0) {
				triples += String.format("<%s> a %s .\n", id,  "wot:Thing");
				triples += String.format("<%s> btea:id <%s> .\n", id,  id);
				continue;
			}
			String type = "anyUri";
			if(childNode.isNumber() || childNode.isFloatingPointNumber())
				type = "float";
			if(childNode.isTextual())
				type = "string";
			if(childNode.isIntegralNumber())
				type = "int";
			if(childNode.isBoolean())
				type = "bool";
			if(val.toString().contains("urn:"))
			{
				triples += String.format("<%s> %s <%s> .\n", id, k, val);
			}
			else
			{
				triples += String.format("<%s> %s \"%s\"^^xsd:%s .\n", id, k, val, type);
			}
		}
		return triples;
	}

	
	private RepositoryConnection getRepositoryConnection() {
		if(repositoryConnection == null || !repositoryConnection.isOpen()) {
		    Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
		    repository.init();
		    repositoryConnection = repository.getConnection();
		    repositoryConnection.begin(); 
		}
	    return repositoryConnection;
	  }
	  
	  private void executeStatement(RepositoryConnection repositoryConnection, String insertStatement) {     
	    Update updateOperation = repositoryConnection
	      .prepareUpdate(QueryLanguage.SPARQL, insertStatement);
	    updateOperation.execute();
	    
	    try {
	      repositoryConnection.commit();
	    } catch (Exception e) {
	      if (repositoryConnection.isActive())
	        repositoryConnection.rollback();
	    }
	  }

	  private void query(RepositoryConnection repositoryConnection) {
	    
	    TupleQuery tupleQuery = repositoryConnection
	      .prepareTupleQuery(QueryLanguage.SPARQL, strQuery);
	    TupleQueryResult result = null;
	    try {
	      result = tupleQuery.evaluate();
	      while (result.hasNext()) {
	        BindingSet bindingSet = result.next();

	        SimpleLiteral name = 
	          (SimpleLiteral)bindingSet.getValue("name");
	        logger.trace("name = " + name.stringValue());
	      }
	    }
	    catch (QueryEvaluationException qee) {
	      logger.error(WTF_MARKER, 
	        qee.getStackTrace().toString(), qee);
	    } finally {
	      result.close();
	    }    
	  }  	
	

	  
	  static {

	    //{"type": "alarm","endpoint":"urn:system1","version":"1.0","timestamp":"2019-10-14T11:47:27",
	    //"message":"High temperature","category":"Maintenance","priority":0,"state":"created",
	    //"createdAt":"2019-10-14T11:47:27","source":"urn:analog_input_01","id":"883574"}
 

	    strInsertThing=
	    		"PREFIX btea: <http://www.bt.schema.siemens.io/custom/btea#>\n"
    			 + "PREFIX wot: <https://www.w3.org/2019/wot/td#>\n"
    			 + "PREFIX btlo: <http://www.bt.schema.siemens.io/custom/btlo#>\n"
    			 + "PREFIX iot: <https://w3id.org/saref#>\n"
    			 + "PREFIX cc: <https://si-ra.github.io/ontologies/cc.owl#> \n"
    			 + "PREFIX ccba: <https://si-ra.github.io/ontologies/ccba.owl#> \n"
		         + "INSERT DATA {\n"
		         + "%triples"
		         + "}";	 
	    
	    strDeleteEvent = 
		        "DELETE WHERE {"
		         + "<%id> ?p ?o ."     
		         + "}";	    
	    	    
	    
	    strQuery = 
	        "SELECT ?thing FROM DEFAULT WHERE {" +
	        "?thing ?p <https://www.w3.org/2019/wot/td#Thing> .}";
	  }	
}
