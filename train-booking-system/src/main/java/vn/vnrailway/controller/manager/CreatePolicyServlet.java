package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.CancellationPolicyRepository;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.CancellationPolicyDAO;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.CancellationPolicy;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Added this import
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CreatePolicyServlet", urlPatterns = { "/createPolicy" })
public class CreatePolicyServlet extends HttpServlet {
    private CancellationPolicyRepository cancellationPolicyRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        cancellationPolicyRepository = new CancellationPolicyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/manager/createPolicy.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validation errors list
        List<String> errors = new ArrayList<>();

        // Get parameters
        String policyName = request.getParameter("policyName");
        String hoursMinStr = request.getParameter("hoursMin");
        String hoursMaxStr = request.getParameter("hoursMax");
        String feePercentageStr = request.getParameter("feePercentage");
        String fixedFeeStr = request.getParameter("fixedFeeAmount");
        String isRefundable = request.getParameter("isRefundable");
        String description = request.getParameter("description");
        String isActive = request.getParameter("isActive");
        String effectiveFromStr = request.getParameter("effectiveFromDate");
        String effectiveToStr = request.getParameter("effectiveToDate");

        try {
            // 1. Validate Policy Name
            if (policyName == null || policyName.trim().isEmpty()) {
                errors.add("Tên chính sách không được để trống");
            } else {
                policyName = normalizeString(policyName);
                if (policyName.length() > 100) {
                    errors.add("Tên chính sách không được vượt quá 100 ký tự");
                } else {
                    // Check duplicate policy name
                    if (isDuplicatePolicyName(policyName)) {
                        errors.add("Tên chính sách '" + policyName + "' đã tồn tại. Vui lòng chọn tên khác");
                    }
                }
            }

            // 2. Validate Hours Before Departure Min
            Integer hoursMin = null;
            if (hoursMinStr == null || hoursMinStr.trim().isEmpty()) {
                errors.add("Số giờ tối thiểu không được để trống");
            } else {
                try {
                    hoursMin = Integer.parseInt(hoursMinStr.trim());
                    if (hoursMin < 0) {
                        errors.add("Số giờ tối thiểu phải >= 0");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Số giờ tối thiểu phải là số nguyên hợp lệ");
                }
            }

            // 3. Validate Hours Before Departure Max
            Integer hoursMax = null;
            if (hoursMaxStr != null && !hoursMaxStr.trim().isEmpty()) {
                try {
                    hoursMax = Integer.parseInt(hoursMaxStr.trim());
                    if (hoursMax < 0) {
                        errors.add("Số giờ tối đa phải >= 0");
                    } else if (hoursMin != null && hoursMax <= hoursMin) {
                        errors.add("Số giờ tối đa phải lớn hơn số giờ tối thiểu");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Số giờ tối đa phải là số nguyên hợp lệ");
                }
            }

            // 4. Validate Fee Percentage
            BigDecimal feePercentage = BigDecimal.ZERO;
            if (feePercentageStr != null && !feePercentageStr.trim().isEmpty()) {
                try {
                    feePercentage = new BigDecimal(feePercentageStr.trim());
                    if (feePercentage.compareTo(BigDecimal.ZERO) < 0) {
                        errors.add("Phần trăm phí không được âm");
                    } else if (feePercentage.compareTo(new BigDecimal("100")) > 0) {
                        errors.add("Phần trăm phí không được vượt quá 100%");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Phần trăm phí phải là số hợp lệ");
                }
            }

            // 5. Validate Fixed Fee Amount
            BigDecimal fixedFee = BigDecimal.ZERO;
            if (fixedFeeStr != null && !fixedFeeStr.trim().isEmpty()) {
                try {
                    fixedFee = new BigDecimal(fixedFeeStr.trim());
                    if (fixedFee.compareTo(BigDecimal.ZERO) < 0) {
                        errors.add("Phí cố định không được âm");
                    } else if (fixedFee.compareTo(new BigDecimal("10000000")) > 0) {
                        errors.add("Phí cố định không được vượt quá 10,000,000 VNĐ");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Phí cố định phải là số hợp lệ");
                }
            }

            // 6. Validate at least one fee type
            boolean hasFeePercentage = feePercentageStr != null && !feePercentageStr.trim().isEmpty()
                    && !feePercentageStr.trim().equals("0");
            boolean hasFixedFee = fixedFeeStr != null && !fixedFeeStr.trim().isEmpty()
                    && !fixedFeeStr.trim().equals("0");

            if (!hasFeePercentage && !hasFixedFee) {
                errors.add("Phải nhập ít nhất một loại phí (phần trăm hoặc cố định) và khác 0");
            }

            // 7. Validate Effective From Date
            Date effectiveFrom = null;
            if (effectiveFromStr == null || effectiveFromStr.trim().isEmpty()) {
                errors.add("Ngày hiệu lực không được để trống");
            } else {
                try {
                    effectiveFrom = Date.valueOf(effectiveFromStr.trim());
                    Date today = new Date(System.currentTimeMillis());

                    if (effectiveFrom.before(today)) {
                        errors.add("Ngày hiệu lực không được trong quá khứ");
                    }
                } catch (IllegalArgumentException e) {
                    errors.add("Định dạng ngày hiệu lực không hợp lệ (yyyy-MM-dd)");
                }
            }

            // 8. Validate Effective To Date
            Date effectiveTo = null;
            if (effectiveToStr != null && !effectiveToStr.trim().isEmpty()) {
                try {
                    effectiveTo = Date.valueOf(effectiveToStr.trim());
                    if (effectiveFrom != null && effectiveTo.before(effectiveFrom)) {
                        errors.add("Ngày kết thúc phải sau hoặc bằng ngày hiệu lực");
                    }
                } catch (IllegalArgumentException e) {
                    errors.add("Định dạng ngày kết thúc không hợp lệ (yyyy-MM-dd)");
                }
            }

            // 9. Validate Description
            if (description != null) {
                description = normalizeString(description);
                if (description.length() > 500) {
                    errors.add("Mô tả không được vượt quá 500 ký tự");
                }
            }

            // 10. Validate isRefundable and isActive
            if (isRefundable == null || (!isRefundable.equals("0") && !isRefundable.equals("1"))) {
                errors.add("Trạng thái hoàn tiền không hợp lệ");
            }

            if (isActive == null || (!isActive.equals("0") && !isActive.equals("1"))) {
                errors.add("Trạng thái kích hoạt không hợp lệ");
            }

            // If there are validation errors, return to form with errors
            if (!errors.isEmpty()) {
                request.setAttribute("validationErrors", errors);
                // Preserve form data
                request.setAttribute("policyName", policyName);
                request.setAttribute("hoursMin", hoursMinStr);
                request.setAttribute("hoursMax", hoursMaxStr);
                request.setAttribute("feePercentage", feePercentageStr);
                request.setAttribute("fixedFeeAmount", fixedFeeStr);
                request.setAttribute("isRefundable", isRefundable);
                request.setAttribute("description", description);
                request.setAttribute("isActive", isActive);
                request.setAttribute("effectiveFromDate", effectiveFromStr);
                request.setAttribute("effectiveToDate", effectiveToStr);

                request.getRequestDispatcher("/WEB-INF/jsp/manager/createPolicy.jsp").forward(request, response);
                return;
            }

            // Create policy if validation passes
            CancellationPolicy policy = new CancellationPolicy();
            policy.setPolicyName(policyName.trim());
            policy.setHoursBeforeDepartureMin(hoursMin);
            policy.setHoursBeforeDepartureMax(hoursMax);
            policy.setFeePercentage(feePercentage);
            policy.setFixedFeeAmount(fixedFee);
            policy.setRefundable("1".equals(isRefundable));
            policy.setDescription(description != null ? description.trim() : "");
            policy.setActive("1".equals(isActive));
            policy.setEffectiveFromDate(effectiveFrom);
            policy.setEffectiveToDate(effectiveTo);

            // Insert policy
            cancellationPolicyRepository.insert(policy);

            // Success - redirect with success message
            request.getSession().setAttribute("successMessage", "Tạo chính sách thành công!");
            response.sendRedirect(request.getContextPath() + "/manageCancellationPolicies");

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            // Preserve form data on database error
            request.getRequestDispatcher("/WEB-INF/jsp/manager/createPolicy.jsp").forward(request, response);
            e.printStackTrace();
        }
    }

    private String normalizeString(String input) {
        if (input == null)
            return null;

        // Trim leading/trailing spaces and replace multiple spaces with single space
        return input.trim().replaceAll("\\s+", " ");
    }

    private boolean isDuplicatePolicyName(String policyName) throws SQLException {
        try {
            // Query to check existing policy names (case-insensitive)
            List<CancellationPolicy> existingPolicies = cancellationPolicyRepository.getAll();

            for (CancellationPolicy policy : existingPolicies) {
                if (policy.getPolicyName().trim().equalsIgnoreCase(policyName.trim())) {
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            throw new SQLException("Lỗi kiểm tra tên chính sách trùng lặp: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws SQLException {
        CancellationPolicyRepository cancellationPolicyRepository = new CancellationPolicyDAO();
        List<CancellationPolicy> existingPolicies = cancellationPolicyRepository.getAll();

        for (CancellationPolicy policy : existingPolicies) {
            System.out.println(policy.getPolicyName().trim());
        }
    }
}
