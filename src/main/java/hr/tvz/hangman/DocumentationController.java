package hr.tvz.hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class DocumentationController implements Initializable {
    private final String FILE_PATH = "src/main/resources/file.html";
    @FXML
    private WebView webView;
    private WebEngine engine;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generate();
        engine = webView.getEngine();
        loadPage();
    }

    public void loadPage(){
        File f = new File(FILE_PATH);
        engine.load(f.toURI().toString());
    }

    public void generate() {
        Path start = Paths.get(".");

        List<Path> classFilePaths = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><title>Documentation</title></head>");
        stringBuilder.append("<body><h1>Documentation for Hangman!</h1>");

        try (Stream<Path> stream = Files.walk(start)) {
            classFilePaths.addAll(stream.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Path path : classFilePaths) {

            String className = path.getFileName().toString();

            List<String> pathElements = new ArrayList<>();
            path.forEach(p -> pathElements.add(p.toString()));

            if (className.endsWith("class") && !className.equals("module-info.class")) {
                stringBuilder.append("<hr>");
                stringBuilder.append("<h2> Class: ");
                stringBuilder.append(path.getFileName());
                stringBuilder.append("</h2>");

                try {
                    String packageName = "";
                    Boolean startPackage = false;

                    for (String pathElement : pathElements) {
                        if (startPackage) {
                            packageName += pathElement + ".";
                            continue;
                        }
                        if (!pathElement.equals("classes")) {
                            continue;
                        } else {
                            startPackage = true;
                        }
                    }

                    Class myClass = Class.forName(packageName.substring(0, packageName.length() - 7));
                    Constructor[] constructors = myClass.getConstructors();
                    Field[] fields = myClass.getFields();
                    Method[] methods = myClass.getMethods();
                    String superclass = myClass.getSuperclass().getName();

                    stringBuilder.append("<h3> Superclass: ");
                    stringBuilder.append(superclass);
                    stringBuilder.append("</h3>");

                    stringBuilder.append("<h2> CONSTRUCTORS </h2>");
                    for (Constructor c : constructors) {
                        stringBuilder.append("<h3>");
                        stringBuilder.append(c.getName());
                        stringBuilder.append("</h3>");
                    }
                    stringBuilder.append("<h2> METHODS </h2>");
                    for (Method m : methods){
                        stringBuilder.append("<h4>");
                        stringBuilder.append(m.getName());
                        stringBuilder.append("() </h4>");
                    }
                    stringBuilder.append("<h2> FIELDS </h2>");
                    for (Field f : fields){
                        stringBuilder.append("<h4>");
                        stringBuilder.append(f.getName());
                        stringBuilder.append("</h4>");
                    }

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        }

        stringBuilder.append("</body></html>");

        try {
            FileWriter fileWriter = new FileWriter(FILE_PATH);
            fileWriter.write(stringBuilder.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
