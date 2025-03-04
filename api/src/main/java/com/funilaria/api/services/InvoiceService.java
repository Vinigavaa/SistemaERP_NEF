package com.funilaria.api.services;

import com.funilaria.api.enums.PecaEnum;
import com.funilaria.api.models.Work;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private static final String CNPJ_EMPRESA = "50.558.407/0001-07";  // CNPJ da empresa
    private static final String PIX_EMPRESA = "50.558407000107@pix.com.br";  // Chave PIX

    public void generateInvoice(Work work) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 750);

        // Título
        contentStream.showText("Nota Fiscal de Serviço");
        contentStream.newLineAtOffset(0, -20);

        // Dados do cliente
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.showText("Cliente: " + work.getClientName());
        contentStream.newLineAtOffset(0, -20);

        contentStream.showText("Modelo do Carro: " + work.getCarModel());
        contentStream.newLineAtOffset(0, -20);

        contentStream.showText("Placa do Carro: " + work.getCarPlate());
        contentStream.newLineAtOffset(0, -20);

        contentStream.showText("Cor do Carro: " + work.getCarColor());
        contentStream.newLineAtOffset(0, -20);

        contentStream.showText("Data do Serviço: " + work.getServiceDate());
        contentStream.newLineAtOffset(0, -20);

        // Repuestos
        contentStream.showText("Peças Reparadas: ");
        List<PecaEnum> repairedParts = work.getRepairedParts();
        for (PecaEnum part : repairedParts) {
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("- " + part);
        }
        contentStream.newLineAtOffset(0, -20);

        // Total
        contentStream.showText("Total: R$ " + work.getTotalPrice());
        contentStream.newLineAtOffset(0, -20);

        // Dados da Empresa (CNPJ e PIX)
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.showText("CNPJ: " + CNPJ_EMPRESA);
        contentStream.newLineAtOffset(0, -20);

        contentStream.showText("Chave PIX: " + PIX_EMPRESA);
        contentStream.endText();
        contentStream.close();

        // Salvar o PDF no diretório
        String outputPath = "nota_fiscal_" + work.getCarPlate() + ".pdf";
        document.save(outputPath);
        document.close();

        System.out.println("PDF gerado com sucesso: " + outputPath);
    }
}
