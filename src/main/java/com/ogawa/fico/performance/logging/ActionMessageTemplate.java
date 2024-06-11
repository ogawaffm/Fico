package com.ogawa.fico.performance.logging;

import com.ogawa.fico.messagetemplate.MessageTemplate;
import com.ogawa.fico.messagetemplate.PreparedMessageTemplate;
import org.slf4j.event.Level;
import lombok.Getter;

/**
 * Implements an immutable message template for progress logging. The following template variables are available:
 * <p></p>
 * <p><code>${TotalUnits}</code> is replaced by the total number of units processed so far. </p>
 * <p><code>${TotalDuration}</code> is replaced by the total duration of the processing so far.</p>
 * <p><code>${TotalThroughputUnits}</code> is replaced by the total number of units processed per time unit so far.</p>
 * <p></p>
 * <p><code>${BatchUnits}</code> is replaced by the number of units processed in the batch.</p>
 * <p><code>${BatchDuration}</code> is replaced by the duration of the processing in the last batch. </p>
 * <p><code>${BatchThroughputUnits}</code> is replaced by the number of units processed per time unit in the last
 * batch.
 * </p>
 * <p></p>
 * <p><code>${ThroughputTimeUnit}</code> is replaced by the time unit used for the throughput. </p>
 * <table VALIGN=TOP ALIGN=CENTER border="1" class="striped">
 * <caption style="display:none">Shows property keys and associated values</caption>
 * <thead>
 * <tr><th scope="col">Category</th><th scope="col">Variable</th><th scope="col">Description</th></tr>
 * </thead>
 * <tbody>
 * <tr>
 *     <td rowspan="2">Java Version</td>
 *     <td>{@code JavaVersion}</td>
 *     <td>Java Runtime Environment version from system property {@code java.version}</td>
 * </tr>
 * <tr>
 *     <td>{@code JavaVersionDate}</td>
 *     <td>Java Runtime Environment version date, in ISO-8601 YYYY-MM-DD format</td>
 * </tr>
 * <tr>
 *     <td rowspan="2">Java Vendor</td>
 *     <td>{@code JavaVendor}</td>
 *     <td>Java Runtime Environment vendor from system property {@code java.vendor}</td>
 * </tr>
 * <tr>
 *     <td>{@code JavaVendorVersion}</td>
 *     <td>Java vendor version from system property {@code java.vendor.version}</td>
 * </tr>
 *
 * <tr>
 *     <td rowspan="3">Java VM Specification</td>
 *     <td>{@code JavaVmSpecificationName}</td>
 *     <td>Java Virtual Machine specification name from system property {@code java.vm.specification.name}</td>
 * </tr>
 * <tr>
 *     <td>{@code JavaVmSpecificationVersion}</td>
 *     <td>Java Virtual Machine specification version from system property {@code java.vm.specification.version}
 *     </td>
 * </tr>
 * <tr>
 *     <td>{@code javaVmSpecificationVendor}</td>
 *     <td>Java Virtual Machine specification vendor from system property {@code java.vm.specification.vendor}</td>
 * </tr>
 *
 * <tr>
 *     <td rowspan="3">Java VM</td>
 *     <td>{@code JavaVmName}</td>
 *     <td>Java Virtual Machine implementation name from system property {@code java.vm.name}</td>
 * </tr>
 * <tr>
 *     <td>{@code JavaVmVersion}</td>
 *     <td>Java Virtual Machine implementation version from system property {@code java.vm.version}</td>
 * </tr>
 * <tr>
 *     <td>{@code javaVmVendor}</td>
 *     <td>Java Virtual Machine implementation vendor from system property {@code java.vm.vendor}</td>
 * </tr>
 *
 * <tr>
 *     <td rowspan="3">Java Specification</td>
 *     <td>{@code JavaSpecificationName}</td>
 *     <td>Java Runtime Environment specification name from system property {@code java.specification.name}</td>
 * </tr>
 * <tr>
 *     <td>{@code JavaSpecificationVersion}</td>
 *     <td>Java Runtime Environment specification version from system property {@code java.specification.version}</td>
 * </tr>
 * <tr>
 *     <td>{@code JavaSpecificationVendor}</td>
 *     <td>Java Runtime Environment specification vendor from system property {@code java.specification.vendor}</td>
 * </tr>
 *
 * <tr>
 *     <td rowspan="3">Operation System</td>
 *     <td>{@code OsName}</td>
 *     <td>Operating system name from system property {@code os.name}</td>
 * </tr>
 * <tr>
 *     <td>{@code OsVersion}</td>
 *     <td>Operating system version from system property {@code os.version}</td>
 * </tr>
 * <tr>
 *     <td>{@code OsArchitecture}</td>
 *     <td>Operating system architecture from system property {@code os.arch}</td>
 * </tr>
 *
 * <tr>
 *     <td>Environment</td>
 *     <td>{@code Username}</td>
 *     <td>User's account name from system property {@code user.name}</td>
 * </tr>
 * </tbody>
 * </table>
 **/
@Getter
public class ActionMessageTemplate {

    protected Level logLevel;
    protected PreparedMessageTemplate<ActionLogger> preparedMessageTemplate;

    public ActionMessageTemplate(Level logLevel, PreparedMessageTemplate<ActionLogger> preparedMessageTemplate) {
        this.preparedMessageTemplate = preparedMessageTemplate;
        this.logLevel = logLevel;
    }

    /* TODO
        @Override
        public ActionMessageTemplate clone() {
            return new ActionMessageTemplate(bulkLogLevel, g);
        }
    */
    public String toString() {
        return logLevel.toString() + " " + preparedMessageTemplate.toString();
    }

}
