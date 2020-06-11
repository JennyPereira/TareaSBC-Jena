/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tareassbc;

import com.csvreader.CsvReader;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import java.io.File;
import java.io.FileOutputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD;
/**
 *
 * @author Jenny
 */
public class Tarea1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        Tarea1 obj = new Tarea1();
        List<Personas> person = new ArrayList<Personas>();
        int i=0;
        person = obj.leer();
        // TODO code application logic here
        Model model = ModelFactory.createDefaultModel();
        try {
            File f= new File ("personas.rdf"); 
            FileOutputStream os = new FileOutputStream(f);
            
            String dataPrefix = "http://example.org/data/";
            model.setNsPrefix("myData",dataPrefix);
            
            //Fijar prefijos de vocabularios incorporados en Jena
            String foaf = "http://xmlns.com/foaf/0.1/";
            model.setNsPrefix("foaf",foaf);
            
            //Fijar Prefijo para otros vocabularios como DBPedia que no están directamente incorporados en Jena
            String dbo = "http://dbpedia.org/ontology/";
            model.setNsPrefix("dbo", dbo);
            Model dboModel = ModelFactory.createDefaultModel();
            
            //Fijar Prefijo para otros vocabularios como SCHEMA que no están directamente incorporados en Jena
            String schema = "http://schema.org/";
            model.setNsPrefix("schema", schema);
            Model schemaModel = ModelFactory.createDefaultModel();
            
            //Crea modelo de modalidad, es decir la tripleta de EstudiantePresencial
            Resource modalidad = model.createResource(dataPrefix+"Subject:EstudiantePresencial")
                 .addProperty(RDFS.label, "Estudiante de Modalidad Presencial") 
                 .addProperty(RDF.type, dboModel.getResource(dbo + "PersonFunction") );
            
            //este for debe permitir añadir el modelo de cada usuario con sus propiedades, pero actualmente no lo hace
            //Algo en el código esta mal, REVISARLO
            for (Personas personas : person) {
                i++;
                //Crea el modelo de Persona, es decir las tripletas de cada persona con sus respectivos atributos
                Resource person1
                        =model.createResource(dataPrefix+personas.getId())
                 .addProperty(RDF.type, FOAF.Person)
                 .addProperty(FOAF.firstName, personas.getNombre()) //***se debe tener solo el NOMBRE o NOMBRES 
                 .addProperty(FOAF.lastName, personas.getNombre())//***se debe tener solo el APELLIDO o APELLIDOS
                 .addProperty(schemaModel.getProperty(schema+"#mail"), personas.getCorreo())
                 .addProperty(dboModel.getProperty(dbo+"country"), personas.getPais())
                 .addProperty(dboModel.getProperty(dbo+"ocupation"), dataPrefix+personas.getOcupacion())//***Esta parte creo que esta mal, REVISAR
                 .addProperty(dboModel.getProperty(dbo+"mother"), dataPrefix+"99234234N")//***Esta parte creo que esta mal, REVISAR
                 
                ;
                model.add(person1, DCTerms.subject, modalidad);
            }
            
        model.write(System.out);
        model.write(System.out, "N3");
        RDFWriter writer = model.getWriter("RDF/XML");
	writer.write(model, os,  "");
        
        //Close model
        model.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Personas> leer(){
        List<Personas> persons = new ArrayList<Personas>();
        try {
            CsvReader leerArchivo = new CsvReader("Data Person.csv");
            leerArchivo.readHeaders();
            
            while(leerArchivo.readRecord()){
                String id = leerArchivo.get(0);
                String nombre = leerArchivo.get(1);
                String pais = leerArchivo.get(2);
                String codigPais = leerArchivo.get(3);
                String correo = leerArchivo.get(4);
                String idPadre = leerArchivo.get(5);
                String idMadre = leerArchivo.get(6);
                String Ocupacion = leerArchivo.get(7);
                
                persons.add(new Personas(id, nombre, pais, codigPais, correo, idPadre, idMadre, Ocupacion));
            }
            leerArchivo.close();
            
            for (Personas person : persons) {
                System.out.println(person.getId() + ", " + person.getNombre() + ", "
                + person.getPais() + ", " + person.getCodigPais() + ", " + person.getCorreo() +
                        ", " + person.getIdPadre() + ", " + person.getIdMadre() + ", " +
                        person.getOcupacion());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException io){
            io.printStackTrace();
        }
        
        
        return persons;
    }
}

//En esta clase se debe añadir el atributo de apellido, una vez se rectifique el error del CSV por no estar separadis los noombres y apellidos
class Personas{
    private String id;
    private String nombre;
    private String apellido;
    private String pais;
    private String codigPais;
    private String correo;
    private String idPadre;
    private String idMadre;
    private String Ocupacion;

    public Personas() {
    }

    public Personas(String id, String nombre, String pais, String codigPais, String correo, String idPadre, String idMadre, String Ocupacion) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.codigPais = codigPais;
        this.correo = correo;
        this.idPadre = idPadre;
        this.idMadre = idMadre;
        this.Ocupacion = Ocupacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCodigPais() {
        return codigPais;
    }

    public void setCodigPais(String codigPais) {
        this.codigPais = codigPais;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(String idPadre) {
        this.idPadre = idPadre;
    }

    public String getIdMadre() {
        return idMadre;
    }

    public void setIdMadre(String idMadre) {
        this.idMadre = idMadre;
    }

    public String getOcupacion() {
        return Ocupacion;
    }

    public void setOcupacion(String Ocupacion) {
        this.Ocupacion = Ocupacion;
    }
    
    
}
