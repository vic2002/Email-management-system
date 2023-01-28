package db;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.spire.presentation.IAutoShape;
import com.spire.presentation.ISlide;
import com.spire.presentation.ParagraphEx;
import com.spire.presentation.Presentation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private String filesFolder;
    private String previewFolder;
    private String toPdfFolder;
    private List<String> docList = new ArrayList<>();
    private List<String> docNames = new ArrayList<>();

    public FileHandler() {
        String path = System.getProperty("user.dir");
        filesFolder = path + "\\src\\main\\webapp\\files\\";
        toPdfFolder = path + "\\src\\main\\webapp\\files\\toPdfs\\";
        previewFolder = path + "\\src\\main\\webapp\\images\\previews\\";
        System.out.println(filesFolder);
        System.out.println(toPdfFolder);
        System.out.println(previewFolder);
    }
    public String generateFullPath(String fileName) {
        return filesFolder + fileName;
    }

    public String toPdfName(String name) {
        int dot = name.lastIndexOf('.');
        return name.substring(0, dot) + ".pdf";
    }

    public String toJpgName(String name) {
        int dot = name.lastIndexOf('.');
        return name.substring(0, dot) + ".jpg";
    }

    public List<String> getAllFiles() {
        List<String> result = new ArrayList<>();
        File folder = new File(filesFolder);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                result.add(file.getName());
            }
        }
        return result;
    }

    public String extractTextPDF (String fileName) {
        String pdfPath = generateFullPath(fileName);
        String text = null;
        try {
            PDDocument pdf = PDDocument.load(new File(pdfPath));
            if (!pdf.isEncrypted()) {
                text = new PDFTextStripper().getText(pdf);
                pdf.close();
                return text;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "empty";
    }

    public String extractTextDOCXPDF (String fileName) {
        String pdfPath = toPdfFolder + toPdfName(fileName);
        String text = null;
        try {
            PDDocument pdf = PDDocument.load(new File(pdfPath));
            if (!pdf.isEncrypted()) {
                text = new PDFTextStripper().getText(pdf);
                pdf.close();
                return text;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "empty";
    }

    public String extractTextDOCX(String fileName) {
        String docxPath = generateFullPath(fileName);
        File someFile = new File(docxPath);
        InputStream inputStrm = null;
        try {
            inputStrm = new FileInputStream(someFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XWPFDocument docx = null;
        try {
            assert inputStrm != null;
            docx = new XWPFDocument(inputStrm);
            inputStrm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        return extractor.getText();
    }

    public String extractTextPptx(String name) {
        Presentation ppt = new Presentation();

        //load the PowerPoint document
        try {
            ppt.loadFromFile(filesFolder + name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create a StringBuilder object
        StringBuilder buffer = new StringBuilder();

        //loop through the slides and extract text
        for (Object slide : ppt.getSlides()) {
            for (Object shape : ((ISlide) slide).getShapes()) {
                if (shape instanceof IAutoShape) {
                    buffer.append("\r\n");
                    for (Object tp : ((IAutoShape) shape).getTextFrame().getParagraphs()) {
                        buffer.append(((ParagraphEx) tp).getText());
                        buffer.append("\r\n");
                    }
                }
            }
        }

        //write to text file
        String content = buffer.toString();
        return content;
    }


    public void pdfPreview(String folder, String name) {
        try {
            File file = new File(folder + name);
            InputStream inputStream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
            byteArrayOutputStream.write(bytes, 0, bytes.length);
            ByteBuffer buf = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

            PDFFile pdffile = new PDFFile(buf);
            PDFPage page = pdffile.getPage(0);
            int width = (int) page.getBBox().getWidth();
            int height = (int) page.getBBox().getHeight();

            Rectangle rect = new Rectangle(0, 0, width, height);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D bufImageGraphics = bufferedImage.createGraphics();

            Image image = page.getImage(width, height, rect, null, true, true);

            bufImageGraphics.drawImage(image, 0, 0, null);

            String imageName = toJpgName(name);
            ImageIO.write(bufferedImage, "jpg", new File(previewFolder + imageName));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("pdfPreview");
        }
    }

    public void pptxPreview(String name) {
        try {
            Presentation ppt = new Presentation();
            ppt.loadFromFile(filesFolder + name);
            //Save PPT document to images
            BufferedImage image = ppt.getSlides().get(0).saveAsImage();
            String imageName = toJpgName(name);
            ImageIO.write(image, "jpg", new File(previewFolder + imageName));
            ppt.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void docxToPdf(String name) {
        try {
            com.aspose.words.Document doc = new com.aspose.words.Document(filesFolder + name);
            String pdfName = toPdfName(name);
            doc.save(toPdfFolder + pdfName);
            com.aspose.pdf.Document pdfDoc = new com.aspose.pdf.Document(toPdfFolder + toPdfName(name));
            pdfDoc.convert("Conversion_log.xml", com.aspose.pdf.PdfFormat.v_1_4, com.aspose.pdf.ConvertErrorAction.Delete);
            File file = new File(toPdfFolder + toPdfName(name));
            file.delete();
            pdfDoc.save(toPdfFolder + toPdfName(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAttachmentsText() {
        List<String> filenames;
        filenames = getAllFiles();
        for (String name : filenames) {
            if (name.endsWith(".pdf")) {
                docNames.add(name);
                docList.add(extractTextPDF(name));
            } else if (name.endsWith(".docx")) {
                docNames.add(name);
                docList.add(extractTextDOCXPDF(name));
            } else if (name.endsWith(".pptx")) {
                docNames.add(name);
                docList.add(extractTextPptx(name));
            }
        }
        DBCall db = new DBCall();
        db.createAttachmentContents(docNames, docList);
    }

    public void convertAllDocxToPdf() {
        List<String> filenames;
        filenames = getAllFiles();
        for (String name : filenames) {
            if (name.endsWith(".docx")) {
                docxToPdf(name);
            }
        }
    }

    public void createPreview(String name) {
        if (name.endsWith(".pdf")) {
            pdfPreview(filesFolder, name);
        } else if (name.endsWith(".docx")) {
            String pdfName = toPdfName(name);
            pdfPreview(toPdfFolder, pdfName);
        } else if (name.endsWith(".pptx")) {
            pptxPreview(name);
        }
    }

    public void getAllPreviews() {
        List<String> filenames;
        filenames = getAllFiles();
        for (String name : filenames) {
            createPreview(name);
            //System.out.println(name);
        }
    }

    public String handleFile(String name) {
        /*System.out.println(1);
        File folder = new File(filesFolder.substring(0, filesFolder.length() - 1));
        System.out.println(2);
        File[] listOfFiles = folder.listFiles();
        System.out.println(listOfFiles.length);

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }*/
        System.out.println("Handling file: " + name);
        String content = "";
        if (name.endsWith(".pdf")) {
            content = extractTextPDF(name);
        } else if (name.endsWith(".docx")) {
            docxToPdf(name);
            content = extractTextDOCXPDF(name);
        } else if (name.endsWith(".pptx")) {
            content = extractTextPptx(name);
        }
        System.out.println(content);
        createPreview(name);
        return content;
    }

    public static void main(String[] args) {
        FileHandler fh = new FileHandler();
    }

}
