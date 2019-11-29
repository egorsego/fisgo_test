package application_manager.api_manager;

import lombok.extern.log4j.Log4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Log4j
public class MenuTree {

    private static final String PATH_TO_TREE = "./src/main/resources/menuTreeKassaF.xml";
    private Document document;

    /**
     * Получить документ
     */
    private void getDocument() {
        if (document == null) {
            createDoc();
        }
    }

    /**
     * создать документ дерева
     */
    private void createDoc() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            document = db.parse(new File(PATH_TO_TREE));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("ParserConfigurationException | SAXException | IOException e", e);
        }
    }

    public String getExpectedParent(String nameLeaf) {
        Node node = getNode(nameLeaf);
        return node.getParentNode().getAttributes().getNamedItem("name").getNodeValue();
    }

    public String getExpectedCurrent(String nameLeaf) {
        Node node = getNode(nameLeaf);
        return node.getAttributes().getNamedItem("name").getNodeValue();
    }

    private Node getNode(String nameLeaf) {
        getDocument();
        if (nameLeaf.equals("root")) {
            return document.getDocumentElement();
        }
        NodeList employeeElements = document.getDocumentElement().getElementsByTagName(nameLeaf);
        return employeeElements.item(0);
    }

    public Node getNode(String nameLeaf, String nameParentLeaf) {
        getDocument();

        if (nameLeaf.equals("root")) {
            return document.getDocumentElement();
        }

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        String expression = "//*[@name='" + nameLeaf + "']";

        NodeList nodeList;
        Node node = null;
        try {
            nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getParentNode().getAttributes().getNamedItem("name").getNodeValue()
                        .equals(nameParentLeaf)) {
                    node = nodeList.item(i);
                    break;
                }
            }
        } catch (XPathExpressionException e) {
            log.error("XPathExpressionException", e);
        }

        return node;
    }

    public List<String> getExpectedListChild(String nameLeaf) {
        getDocument();
        ArrayList<String> expectedListChild = new ArrayList<>();

        NodeList employeeElements = document.getDocumentElement().getElementsByTagName(nameLeaf);
        Node currentNode = employeeElements.item(0);
        NodeList nodeList = currentNode.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if (childNode.getAttributes().getNamedItem("name").getNodeValue().equals("NO ITEMS")) {
                    expectedListChild.add(childNode.getAttributes().getNamedItem("name").getNodeValue());
                } else {
                    expectedListChild.add(childNode.getAttributes().getNamedItem("id").getNodeValue() +
                            "." +
                            childNode.getAttributes().getNamedItem("name").getNodeValue());
                }
            }
        }
        return expectedListChild;
    }

    private int getNestingLevel(Node node) {
        if (node.equals(document.getDocumentElement())) {
            return 0;
        }
        int count = 0;
        while (node.getParentNode() != null) {
            node = node.getParentNode();
            count++;
        }
        return count - 1;
    }

    public List<String> getKeysForGoToItemMenu(Node from, String to) {
        Node nodeFrom = from;
        Node nodeTo = getNode(to);

        //массивы для хранения кнопок
        List<String> keysFromGeneralParent = new ArrayList<>(); //кнопки которые нужно нажать до общего родителя
        LinkedList<String> keysToGeneralParent = new LinkedList<>();//кнопки которые нужно нажать после общего родителя

        //проверка что мы уже не находимся на нужном пункте меню
        if (nodeTo.equals(nodeFrom)) {
            return new ArrayList<>();
        }

        //заполняем массив кнопками если уровни вложенности разные, и двигаем node вверх чтобы выравнить уровни
        //если начальный ниже конечного

        if (getNestingLevel(nodeFrom) > getNestingLevel(nodeTo)) {
            while (getNestingLevel(nodeFrom) != getNestingLevel(nodeTo)) {
                keysFromGeneralParent.add("-1");
                nodeFrom = nodeFrom.getParentNode();
            }
        }

        //заполняем массив кнопками если уровни вложенности разные, и двигаем node вверх чтобы выравнить уровни
        //если конечный ниже начального
        if (getNestingLevel(nodeFrom) < getNestingLevel(nodeTo)) {
            while (getNestingLevel(nodeFrom) != getNestingLevel(nodeTo)) {
                keysToGeneralParent.addFirst(nodeTo.getAttributes().getNamedItem("id").getNodeValue());
                nodeTo = nodeTo.getParentNode();
            }
        }

        //проверка на то, что в результате предыдущего шага мы не пришли в нужный пункт меню
        if (nodeFrom.equals(nodeTo)) {
            if (keysFromGeneralParent.isEmpty()) {
                return keysToGeneralParent;
            }
            if (keysToGeneralParent.isEmpty()) {
                return keysFromGeneralParent;
            }

        } else {
            while (!nodeFrom.equals(nodeTo) && getNestingLevel(nodeFrom) != 0) {
                keysFromGeneralParent.add("-1");
                keysToGeneralParent.addFirst(nodeTo.getAttributes().getNamedItem("id").getNodeValue());
                nodeTo = nodeTo.getParentNode();
                nodeFrom = nodeFrom.getParentNode();
            }
        }

        //соединяем полученные кнопки в итоговый массив
        List<String> resultKeys = new ArrayList<>();
        resultKeys.addAll(keysFromGeneralParent);
        resultKeys.addAll(keysToGeneralParent);

        return resultKeys;
    }

}
