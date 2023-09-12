package com.bot.cqs.report.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

/**
 * <pre>
 * AbstractJasperPdfService
 * </pre>
 * 
 * @since 2017年1月5日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月5日,bob peng,new
 *          </ul>
 */
public abstract class AbstractJasperPdfService implements IJasperPdfService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final String REPORT_SUFFIX = ".jasper";

    public ByteArrayOutputStream generateReport(Request request) throws CapException {

        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {// TODO OBU
            // 1
//            JasperCompileManager.compileReportToFile("C:/Users/ASUS/Desktop/jasper/queryDetail.jrxml", "C:/Users/ASUS/Desktop/jasper/queryDetail.jasper");
            Resource resource = new ClassPathResource(getReportDefinition() + REPORT_SUFFIX);
            inputStream = resource.getInputStream();

            // 2
            Map<String, Object> reportParameters = excute(request);
            List<Object> dataList = (List<Object>) reportParameters.get("dataList");

            // 3
            JRDataSource dataSource = new JRBeanCollectionDataSource(dataList);

            // process pdf
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, reportParameters, dataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputstream));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } catch (Exception e) {
            if (e.getCause() != null) {
                throw new CapException(e.getCause(), e.getClass());
            } else {
                throw new CapException(e, e.getClass());
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputstream);
        }
        return outputstream;
    }

    public Map<String, Object> excute(Request request) {
        return null;
    }
}
