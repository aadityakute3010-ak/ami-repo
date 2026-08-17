package com.ami.service.impl;

import com.ami.dto.requests.ApplyLeaveRequestDto;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ami.dto.responses.EngineerActivityResponseDto;
import com.ami.dto.responses.EngineerDashboardResponseDto;
import com.ami.dto.responses.EngineerLeaveResponseDto;
import com.ami.dto.responses.EngineerOperationResponseDto;
import com.ami.dto.responses.EngineerOperationsSummaryResponseDto;
import com.ami.dto.responses.EngineerPerformanceResponseDto;
import com.ami.dto.responses.EngineerStatisticsResponseDto;
import com.ami.dto.responses.EngineerWorkloadResponseDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.entity.EngineerLeave;
import com.ami.entity.Issue;
import com.ami.entity.User;
import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.IssueStatus;
import com.ami.enums.LeaveStatus;
import com.ami.enums.RoleType;
import com.ami.repository.EngineerLeaveRepository;
import com.ami.repository.IssueRepository;
import com.ami.repository.UserRepository;
import com.ami.service.EngineerService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
@Service
public class EngineerServiceImpl
        implements EngineerService {

    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final EngineerLeaveRepository engineerLeaveRepository;

    public EngineerServiceImpl(
            UserRepository userRepository,
            IssueRepository issueRepository,
            EngineerLeaveRepository engineerLeaveRepository) {

        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.engineerLeaveRepository =
                engineerLeaveRepository;
    }

    @Override
    public List<User> getEngineers() {

        return userRepository.findByRole(
                RoleType.SERVICE_ENGINEER);
    }

    @Override
    public User getEngineerById(
            Long engineerId) {

        return userRepository.findById(engineerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Engineer not found"));
    }

    @Override
    public List<User> getAvailableEngineers() {

        return userRepository.findByRoleAndAvailabilityStatus(
                RoleType.SERVICE_ENGINEER,
                EngineerAvailabilityStatus.AVAILABLE);
    }

    @Override
    public EngineerWorkloadResponseDto getWorkload(
            Long engineerId) {

        EngineerWorkloadResponseDto dto =
                new EngineerWorkloadResponseDto();

        dto.setActiveIssues(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.IN_PROGRESS));

        dto.setResolvedIssues(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.RESOLVED));

        dto.setRejectedIssues(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.REJECTED));
        
        dto.setAssignedIssues(
                issueRepository.countByAssignedEngineerId(
                        engineerId));

        dto.setCompletedIssues(
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.CLOSED));

        return dto;
    }
    
    @Override
    public EngineerDashboardResponseDto getDashboard(Long engineerId) {

        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() -> new RuntimeException("Engineer not found"));

        long assignedIssues =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.AUTO_ASSIGNED);

        long acceptedIssues =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.ACCEPTED);

        long inProgressIssues =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.IN_PROGRESS);

        long completedIssues =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.RESOLVED);

        long escalatedIssues =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.ESCALATED);

        long resolvedToday = completedIssues;

        long todayVisits = 0L;

        long pendingSla = 0L;

        double resolutionRate =
                assignedIssues == 0
                        ? 0.0
                        : (completedIssues * 100.0) / assignedIssues;

        return EngineerDashboardResponseDto.builder()

                .assignedIssues(assignedIssues)

                .acceptedIssues(acceptedIssues)

                .inProgressIssues(inProgressIssues)

                .resolvedIssues(completedIssues)

                .completedIssues(completedIssues)

                .escalatedIssues(escalatedIssues)

                .todayVisits(todayVisits)

                .pendingSla(pendingSla)

                .resolvedToday(resolvedToday)

                .resolutionRate(resolutionRate)

                .attendance(engineer.getAttendanceStatus())

                .availability(engineer.getAvailabilityStatus())

                .weeklyPerformance(resolutionRate)

                .monthlyPerformance(resolutionRate)

                .build();
    }
    @Override
    public EngineerPerformanceResponseDto getPerformance(
            Long engineerId) {

        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() ->
                        new RuntimeException("Engineer not found"));

        long assigned =
                issueRepository.countByAssignedEngineerId(
                        engineerId);

        long resolved =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.RESOLVED);

        long rejected =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.REJECTED);

        long escalated =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.ESCALATED);

        long inProgress =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.IN_PROGRESS);
        
        long completed =
                issueRepository.countByAssignedEngineerIdAndStatus(
                        engineerId,
                        IssueStatus.CLOSED);

        long pending =
                assigned - resolved - completed;

        double resolutionRate =
                assigned == 0
                        ? 0.0
                        : (resolved * 100.0) / assigned;

        double slaPerformance =
                assigned == 0
                        ? 0.0
                        : ((assigned - issueRepository.countBySlaBreachedTrue()) * 100.0)
                                / assigned;

        double monthlyPerformance =
                resolutionRate;

        return EngineerPerformanceResponseDto.builder()
                .engineerId(engineer.getId())
                .engineerName(
                        engineer.getFirstName() + " " + engineer.getLastName())
                .assignedIssues(assigned)
                .resolvedIssues(resolved)
                .rejectedIssues(rejected)
                .escalatedIssues(escalated)
                .inProgressIssues(inProgress)
                .resolutionRate(resolutionRate)

                .slaPerformance(slaPerformance)

                .completedJobs(completed)

                .pendingJobs(pending)

                .monthlyPerformance(monthlyPerformance)
                .build();
    }
    @Override
    public List<IssueResponseDto>
    getHistory(
            Long engineerId) {

        return issueRepository
                .findByAssignedEngineerId(
                        engineerId)
                .stream()
                .map(issue -> IssueResponseDto
                        .builder()
                        .id(issue.getId())
                        .ticketNumber(
                                issue.getTicketNumber())
                        .title(
                                issue.getTitle())
                        .status(
                                issue.getStatus())
                        .build())
                .toList();
    }
    @Override
    public List<IssueResponseDto>
    getSchedule(
            Long engineerId) {

        return issueRepository
                .findByAssignedEngineerId(
                        engineerId)
                .stream()
                .filter(issue ->
                        issue.getStatus()
                                == IssueStatus.ACCEPTED
                        || issue.getStatus()
                                == IssueStatus.IN_PROGRESS)
                .map(issue -> IssueResponseDto
                        .builder()

                        .id(issue.getId())

                        .ticketNumber(issue.getTicketNumber())

                        .title(issue.getTitle())

                        .priority(issue.getPriority())

                        .status(issue.getStatus())

                        .customerName(issue.getCustomerName())

                        .customerPhone(issue.getCustomerPhone())

                        .address(issue.getAddress())

                        .city(issue.getCity())

                        .assignedAt(issue.getAssignedAt())

                        .responseDueAt(issue.getResponseDueAt())

                        .resolutionDueAt(issue.getResolutionDueAt())

                        .build())
                .toList();
    }
  
    @Override
    public String updateAttendance(
            Long engineerId,
            EngineerAttendanceStatus status) {

        User engineer =
                userRepository.findById(engineerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Engineer not found"));

        engineer.setAttendanceStatus(status);
        engineer.setLastAttendanceUpdated(
                LocalDateTime.now());

        userRepository.save(engineer);

        return "Attendance updated successfully";
    }
    @Override
    public String updateAvailability(
            Long engineerId,
            EngineerAvailabilityStatus status) {

        User engineer =
                userRepository.findById(engineerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Engineer not found"));

        engineer.setAvailabilityStatus(status);
        engineer.setLastAvailabilityUpdated(
                LocalDateTime.now());

        engineer.setOnField(
                status == EngineerAvailabilityStatus.ON_FIELD);

        userRepository.save(engineer);

        return "Availability updated successfully";
    }
    @Override
    public String applyLeave(
            Long engineerId,
            ApplyLeaveRequestDto request) {

        userRepository.findById(engineerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Engineer not found"));

        EngineerLeave leave =
                EngineerLeave.builder()
                        .engineerId(engineerId)
                        .fromDate(request.getFromDate())
                        .toDate(request.getToDate())
                        .reason(request.getReason())
                        .status(LeaveStatus.PENDING)
                        .appliedAt(LocalDateTime.now())
                        .build();

        engineerLeaveRepository.save(leave);

        return "Leave applied successfully";
    }
    @Override
    public List<EngineerLeaveResponseDto>
    getLeaveHistory(
            Long engineerId) {

        return engineerLeaveRepository
                .findByEngineerId(engineerId)
                .stream()
                .map(leave ->
                        EngineerLeaveResponseDto
                                .builder()
                                .id(leave.getId())
                                .fromDate(leave.getFromDate())
                                .toDate(leave.getToDate())
                                .reason(leave.getReason())
                                .status(leave.getStatus())
                                .build())
                .toList();
    }
    @Override
    public Integer getLeaveBalance(
            Long engineerId) {

        User engineer =
                userRepository.findById(engineerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Engineer not found"));

        return engineer.getLeaveBalance();
    }
    @Override
    public String updateLeaveStatus(
            Long leaveId,
            LeaveStatus status) {

        EngineerLeave leave =
                engineerLeaveRepository
                        .findById(leaveId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave not found"));

        leave.setStatus(status);
        leave.setActionAt(
                LocalDateTime.now());

        engineerLeaveRepository.save(leave);

        return "Leave status updated successfully";
    }
    @Override
    public EngineerOperationsSummaryResponseDto
    getOperationsSummary() {

        return EngineerOperationsSummaryResponseDto
                .builder()
                .totalEngineers(
                        userRepository.countByRole(
                                RoleType.SERVICE_ENGINEER))
                .present(
                        userRepository.countByAttendanceStatus(
                                EngineerAttendanceStatus.PRESENT))
                .absent(
                        userRepository.countByAttendanceStatus(
                                EngineerAttendanceStatus.ABSENT))
                .halfDay(
                        userRepository.countByAttendanceStatus(
                                EngineerAttendanceStatus.HALF_DAY))
                .onLeave(
                        userRepository.countByAttendanceStatus(
                                EngineerAttendanceStatus.LEAVE))
                .available(
                        userRepository.countByAvailabilityStatus(
                                EngineerAvailabilityStatus.AVAILABLE))
                .busy(
                        userRepository.countByAvailabilityStatus(
                                EngineerAvailabilityStatus.BUSY))
                .onField(
                        userRepository.countByAvailabilityStatus(
                                EngineerAvailabilityStatus.ON_FIELD))
                .offline(
                        userRepository.countByAvailabilityStatus(
                                EngineerAvailabilityStatus.OFFLINE))
                .build();
    }
    @Override
    public List<EngineerActivityResponseDto>
    getActivity(
            Long engineerId) {

        return issueRepository
                .findByAssignedEngineerId(
                        engineerId)
                .stream()
                .map(issue ->
                        EngineerActivityResponseDto
                                .builder()
                                .activity(
                                        issue.getStatus().name())
                                .issueNumber(
                                        issue.getTicketNumber())
                                .activityTime(
                                        issue.getUpdatedAt())
                                .build())
                .toList();
    }
    
    @Override
    public EngineerStatisticsResponseDto
    getStatistics() {

        return EngineerStatisticsResponseDto
                .builder()
                .totalEngineers(
                        userRepository.countByRole(
                                RoleType.SERVICE_ENGINEER))
                .totalAssignedIssues(
                        issueRepository.count())
                .totalResolvedIssues(
                        issueRepository.countByStatus(
                                IssueStatus.RESOLVED))
                .totalRejectedIssues(
                        issueRepository.countByStatus(
                                IssueStatus.REJECTED))
                .totalEscalatedIssues(
                        issueRepository.countByStatus(
                                IssueStatus.ESCALATED))
                .availableEngineers(
                        userRepository.countByAvailabilityStatus(
                                EngineerAvailabilityStatus.AVAILABLE))
                .busyEngineers(
                        userRepository.countByAvailabilityStatus(
                                EngineerAvailabilityStatus.BUSY))
                .onLeaveEngineers(
                        userRepository.countByAttendanceStatus(
                                EngineerAttendanceStatus.LEAVE))
                .build();
    }
    @Override
    public Page<EngineerOperationResponseDto> getEngineerOperations(

            String search,

            EngineerAttendanceStatus attendanceStatus,

            EngineerAvailabilityStatus availabilityStatus,

            int page,

            int size,

            String sortBy,

            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort);

        Page<User> users = userRepository.findEngineerOperations(
                search,
                attendanceStatus,
                availabilityStatus,
                pageable);

        return users.map(user -> {

            Issue issue = issueRepository
                    .findTopByAssignedEngineerIdOrderByUpdatedAtDesc(
                            user.getId())
                    .orElse(null);

            return EngineerOperationResponseDto.builder()

                    .engineerName(
                            user.getFirstName() + " " + user.getLastName())

                    .issueId(
                            issue != null ? issue.getId() : null)

                    .ticketNumber(
                            issue != null ? issue.getTicketNumber() : null)

                    .issueTitle(
                            issue != null ? issue.getTitle() : null)

                    .priority(
                            issue != null ? issue.getPriority().name() : null)

                    .status(
                            issue != null ? issue.getStatus().name() : null)

                    .currentLocation(
                            issue != null ? issue.getCity() : user.getCity())

                    .visitStatus(
                            issue != null && issue.getStartedAt() != null
                                    ? "ON_SITE"
                                    : "NOT_STARTED")

                    .slaStatus(
                            issue != null ? issue.getSlaStatus() : null)

                    .lastUpdated(
                            issue != null ? issue.getUpdatedAt() : null)

                    .build();
        });
    }
    @Override
    public byte[] exportEngineersCsv() {

        StringBuilder csv =
                new StringBuilder();

        csv.append("Id,Name,Email,Attendance,Availability\n");

        List<User> engineers =
                userRepository.findByRole(
                        RoleType.SERVICE_ENGINEER);

        for (User engineer : engineers) {

            csv.append(engineer.getId()).append(",");

            csv.append(engineer.getFirstName())
                    .append(" ")
                    .append(engineer.getLastName())
                    .append(",");

            csv.append(engineer.getEmail())
                    .append(",");

            csv.append(engineer.getAttendanceStatus())
                    .append(",");

            csv.append(engineer.getAvailabilityStatus())
                    .append("\n");
        }

        return csv.toString()
                .getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public byte[] exportEngineersExcel() {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Engineers");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("First Name");
            header.createCell(2).setCellValue("Last Name");
            header.createCell(3).setCellValue("Email");
            header.createCell(4).setCellValue("Attendance");
            header.createCell(5).setCellValue("Availability");

            List<User> engineers =
                    userRepository.findByRole(
                            RoleType.SERVICE_ENGINEER);

            int rowNum = 1;

            for (User engineer : engineers) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(engineer.getId());

                row.createCell(1).setCellValue(
                        engineer.getFirstName());

                row.createCell(2).setCellValue(
                        engineer.getLastName());

                row.createCell(3).setCellValue(
                        engineer.getEmail());

                row.createCell(4).setCellValue(
                        engineer.getAttendanceStatus() == null
                                ? ""
                                : engineer.getAttendanceStatus().name());

                row.createCell(5).setCellValue(
                        engineer.getAvailabilityStatus() == null
                                ? ""
                                : engineer.getAvailabilityStatus().name());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export Excel", e);
        }
    }
    @Override
    public byte[] exportEngineersPdf() {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    18);

            Paragraph title = new Paragraph(
                    "Service Engineers Report",
                    titleFont);

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);

            table.setWidthPercentage(100);

            table.addCell("ID");
            table.addCell("First Name");
            table.addCell("Last Name");
            table.addCell("Email");
            table.addCell("Attendance");
            table.addCell("Availability");

            List<User> engineers =
                    userRepository.findByRole(
                            RoleType.SERVICE_ENGINEER);

            for (User engineer : engineers) {

                table.addCell(String.valueOf(engineer.getId()));

                table.addCell(engineer.getFirstName());

                table.addCell(engineer.getLastName());

                table.addCell(engineer.getEmail());

                table.addCell(
                        engineer.getAttendanceStatus() == null
                                ? ""
                                : engineer.getAttendanceStatus().name());

                table.addCell(
                        engineer.getAvailabilityStatus() == null
                                ? ""
                                : engineer.getAvailabilityStatus().name());
            }

            document.add(table);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export PDF",
                    e);
        }
    }
    @Override
    public byte[] exportEngineer(
            Long engineerId,
            String format) {

        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() ->
                        new RuntimeException("Engineer not found"));

        switch (format.toLowerCase()) {

            case "csv":

                StringBuilder csv = new StringBuilder();

                csv.append("Id,Name,Email,Attendance,Availability\n");

                csv.append(engineer.getId()).append(",");

                csv.append(engineer.getFirstName())
                        .append(" ")
                        .append(engineer.getLastName())
                        .append(",");

                csv.append(engineer.getEmail())
                        .append(",");

                csv.append(engineer.getAttendanceStatus())
                        .append(",");

                csv.append(engineer.getAvailabilityStatus());

                return csv.toString().getBytes(StandardCharsets.UTF_8);

            case "excel":

                try (Workbook workbook = new XSSFWorkbook();
                     ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                    Sheet sheet = workbook.createSheet("Engineer");

                    Row header = sheet.createRow(0);

                    header.createCell(0).setCellValue("ID");
                    header.createCell(1).setCellValue("First Name");
                    header.createCell(2).setCellValue("Last Name");
                    header.createCell(3).setCellValue("Email");
                    header.createCell(4).setCellValue("Attendance");
                    header.createCell(5).setCellValue("Availability");

                    Row row = sheet.createRow(1);

                    row.createCell(0).setCellValue(engineer.getId());
                    row.createCell(1).setCellValue(engineer.getFirstName());
                    row.createCell(2).setCellValue(engineer.getLastName());
                    row.createCell(3).setCellValue(engineer.getEmail());

                    row.createCell(4).setCellValue(
                            engineer.getAttendanceStatus() == null
                                    ? ""
                                    : engineer.getAttendanceStatus().name());

                    row.createCell(5).setCellValue(
                            engineer.getAvailabilityStatus() == null
                                    ? ""
                                    : engineer.getAvailabilityStatus().name());

                    for (int i = 0; i < 6; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    workbook.write(out);

                    return out.toByteArray();

                } catch (Exception e) {

                    throw new RuntimeException(
                            "Failed to export Excel",
                            e);
                }

            case "pdf":

                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                    Document document = new Document(PageSize.A4);

                    PdfWriter.getInstance(document, out);

                    document.open();

                    Font titleFont = FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18);

                    Paragraph title = new Paragraph(
                            "Service Engineer Report",
                            titleFont);

                    title.setAlignment(Element.ALIGN_CENTER);

                    document.add(title);

                    document.add(new Paragraph(" "));

                    PdfPTable table = new PdfPTable(2);

                    table.setWidthPercentage(100);

                    table.addCell(new PdfPCell(new Paragraph("Field")));
                    table.addCell(new PdfPCell(new Paragraph("Value")));

                    table.addCell("ID");
                    table.addCell(String.valueOf(engineer.getId()));

                    table.addCell("First Name");
                    table.addCell(engineer.getFirstName());

                    table.addCell("Last Name");
                    table.addCell(engineer.getLastName());

                    table.addCell("Email");
                    table.addCell(engineer.getEmail());

                    table.addCell("Attendance");
                    table.addCell(
                            engineer.getAttendanceStatus() == null
                                    ? ""
                                    : engineer.getAttendanceStatus().name());

                    table.addCell("Availability");
                    table.addCell(
                            engineer.getAvailabilityStatus() == null
                                    ? ""
                                    : engineer.getAvailabilityStatus().name());

                    document.add(table);

                    document.close();

                    return out.toByteArray();

                } catch (Exception e) {

                    throw new RuntimeException(
                            "Failed to export PDF",
                            e);
                }

            default:

                throw new RuntimeException("Unsupported export format : " + format);
        }
    }
}