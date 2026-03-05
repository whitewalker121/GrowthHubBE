//package com.growthhub.salon.repository;
//
//import com.growthhub.salon.entity.*;
//import com.growthhub.salon.enums.*;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.*;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.*;
//
//// ─────────────────────────────────────────────────────────────
//// USER
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface UserRepository extends JpaRepository<User, UUID> {
//    Optional<User> findByEmail(String email);
//    boolean existsByEmail(String email);
//}
//
//// ─────────────────────────────────────────────────────────────
//// CLIENT
//// ─────────────────────────────────────────────────────────────
//@Repository
//public
//interface ClientRepository extends JpaRepository<Client, UUID> {
//
//    Optional<Client> findByPhone(String phone);
//    boolean existsByPhone(String phone);
//
//    @Query("SELECT c FROM Client c WHERE " +
//           "LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
//           "c.phone LIKE CONCAT('%', :q, '%') OR " +
//           "LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%'))")
//    Page<Client> search(@Param("q") String query, Pageable pageable);
//
//    Page<Client> findByMembershipType(ClientTier tier, Pageable pageable);
//
//    @Query("SELECT c FROM Client c WHERE c.lastVisitAt < :cutoff AND c.isActive = true")
//    List<Client> findInactiveClients(@Param("cutoff") LocalDate cutoff);
//
//    @Query("SELECT COUNT(c) FROM Client c WHERE c.joinDate BETWEEN :from AND :to")
//    long countNewClients(@Param("from") LocalDate from, @Param("to") LocalDate to);
//}
//
//// ─────────────────────────────────────────────────────────────
//// STAFF
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface StaffRepository extends JpaRepository<Staff, UUID> {
//    List<Staff> findByStatus(StaffStatus status);
//
//    @Query("SELECT s FROM Staff s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :q, '%'))")
//    List<Staff> search(@Param("q") String query);
//}
//
//// ─────────────────────────────────────────────────────────────
//// SERVICE CATEGORY
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, UUID> {
//    List<ServiceCategory> findByIsActiveTrueOrderBySortOrderAsc();
//}
//
//// ─────────────────────────────────────────────────────────────
//// SERVICE
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface ServiceRepository extends JpaRepository<Service, UUID> {
//    Page<Service> findByStatus(ServiceStatus status, Pageable pageable);
//    List<Service> findByCategoryIdAndStatus(UUID categoryId, ServiceStatus status);
//    List<Service> findByIsPopularTrueAndStatus(ServiceStatus status);
//
//    @Query("SELECT s FROM Service s WHERE " +
//           "LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) AND s.status = 'ACTIVE'")
//    Page<Service> search(@Param("q") String query, Pageable pageable);
//}
//
//// ─────────────────────────────────────────────────────────────
//// APPOINTMENT
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
//
//    Page<Appointment> findByAppointmentDateOrderByStartTimeAsc(LocalDate date, Pageable pageable);
//
//    List<Appointment> findByAppointmentDateAndStatusIn(LocalDate date, List<AppointmentStatus> statuses);
//
//    @Query("SELECT a FROM Appointment a WHERE a.client.id = :clientId ORDER BY a.appointmentDate DESC, a.startTime DESC")
//    Page<Appointment> findByClientId(@Param("clientId") UUID clientId, Pageable pageable);
//
//    @Query("SELECT a FROM Appointment a WHERE a.staff.id = :staffId AND a.appointmentDate = :date ORDER BY a.startTime ASC")
//    List<Appointment> findByStaffAndDate(@Param("staffId") UUID staffId, @Param("date") LocalDate date);
//
//    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date AND a.status NOT IN ('CANCELLED','NO_SHOW')")
//    long countActiveByDate(@Param("date") LocalDate date);
//
//    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :from AND :to AND a.status = :status")
//    List<Appointment> findByDateRangeAndStatus(@Param("from") LocalDate from,
//                                               @Param("to") LocalDate to,
//                                               @Param("status") AppointmentStatus status);
//
//    // Check staff availability (no overlapping appointments)
//    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.staff.id = :staffId " +
//           "AND a.appointmentDate = :date AND a.status NOT IN ('CANCELLED','NO_SHOW') " +
//           "AND ((a.startTime < :end) AND (a.endTime > :start))")
//    boolean isStaffBusy(@Param("staffId") UUID staffId,
//                        @Param("date") LocalDate date,
//                        @Param("start") java.time.LocalTime start,
//                        @Param("end") java.time.LocalTime end);
//}
//
//// ─────────────────────────────────────────────────────────────
//// INVOICE
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
//
//    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
//
//    Page<Invoice> findByClientIdOrderByInvoiceDateDesc(UUID clientId, Pageable pageable);
//
//    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.invoiceDate BETWEEN :from AND :to AND i.status = 'PAID'")
//    Optional<BigDecimal> sumRevenueByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
//
//    @Query("SELECT i.paymentMethod, SUM(i.totalAmount) FROM Invoice i " +
//           "WHERE i.invoiceDate BETWEEN :from AND :to AND i.status = 'PAID' " +
//           "GROUP BY i.paymentMethod")
//    List<Object[]> revenueByPaymentMethod(@Param("from") LocalDate from, @Param("to") LocalDate to);
//
//    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate = :date AND i.status = 'PAID'")
//    long countPaidByDate(@Param("date") LocalDate date);
//
//    // Generate next invoice number
//    @Query(value = "SELECT COALESCE(MAX(CAST(SPLIT_PART(invoice_number, '-', 3) AS INT)), 0) + 1 " +
//                   "FROM invoices WHERE invoice_number LIKE :prefix || '-%'", nativeQuery = true)
//    int nextSequence(@Param("prefix") String prefix);
//}
//
//// ─────────────────────────────────────────────────────────────
//// INVENTORY
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
//
//    Page<InventoryItem> findByCategoryId(UUID categoryId, Pageable pageable);
//    List<InventoryItem> findByStatus(InventoryStatus status);
//    Optional<InventoryItem> findBySku(String sku);
//
//    @Query("SELECT i FROM InventoryItem i WHERE " +
//           "LOWER(i.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
//           "LOWER(i.brand) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
//           "LOWER(i.sku) LIKE LOWER(CONCAT('%', :q, '%'))")
//    Page<InventoryItem> search(@Param("q") String query, Pageable pageable);
//
//    @Query("SELECT i FROM InventoryItem i WHERE i.currentStock <= i.minStockLevel AND i.status != 'DISCONTINUED'")
//    List<InventoryItem> findLowStockItems();
//
//    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate <= :cutoff AND i.status != 'DISCONTINUED'")
//    List<InventoryItem> findExpiringItems(@Param("cutoff") LocalDate cutoff);
//}
//
//@Repository
//interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
//    List<StockMovement> findByItemIdOrderByCreatedAtDesc(UUID itemId);
//}
//
//// ─────────────────────────────────────────────────────────────
//// EXPENSE
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface ExpenseRepository extends JpaRepository<Expense, UUID> {
//
//    Page<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(LocalDate from, LocalDate to, Pageable pageable);
//    Page<Expense> findByCategoryIdAndExpenseDateBetween(UUID categoryId, LocalDate from, LocalDate to, Pageable pageable);
//
//    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :from AND :to AND e.status = 'APPROVED'")
//    Optional<BigDecimal> sumApprovedByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
//
//    @Query("SELECT ec.name, SUM(e.amount) FROM Expense e JOIN e.category ec " +
//           "WHERE e.expenseDate BETWEEN :from AND :to AND e.status = 'APPROVED' " +
//           "GROUP BY ec.name ORDER BY SUM(e.amount) DESC")
//    List<Object[]> expensesByCategory(@Param("from") LocalDate from, @Param("to") LocalDate to);
//}
//
//// ─────────────────────────────────────────────────────────────
//// ATTENDANCE
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
//
//    Optional<Attendance> findByStaffIdAndWorkDate(UUID staffId, LocalDate workDate);
//    List<Attendance> findByWorkDate(LocalDate workDate);
//    List<Attendance> findByStaffIdAndWorkDateBetweenOrderByWorkDateAsc(UUID staffId, LocalDate from, LocalDate to);
//
//    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.workDate BETWEEN :from AND :to GROUP BY a.status")
//    List<Object[]> attendanceSummary(@Param("from") LocalDate from, @Param("to") LocalDate to);
//}
//
//// ─────────────────────────────────────────────────────────────
//// LOYALTY
//// ─────────────────────────────────────────────────────────────
//@Repository
//public
//interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, UUID> {
//    List<LoyaltyProgram> findByStatus(String status);
//}
//
//@Repository
//public
//interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, UUID> {
//    List<LoyaltyTransaction> findByClientIdOrderByCreatedAtDesc(UUID clientId);
//    Page<LoyaltyTransaction> findByClientId(UUID clientId, Pageable pageable);
//}
//
//// ─────────────────────────────────────────────────────────────
//// MEMBERSHIP
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface MembershipPackageRepository extends JpaRepository<MembershipPackage, UUID> {
//    List<MembershipPackage> findByStatusOrderBySortOrderAsc(String status);
//}
//
//@Repository
//interface ClientMembershipRepository extends JpaRepository<ClientMembership, UUID> {
//    List<ClientMembership> findByClientIdAndStatus(UUID clientId, MembershipStatus status);
//    Optional<ClientMembership> findFirstByClientIdAndStatusOrderByExpiryDateDesc(UUID clientId, MembershipStatus status);
//
//    @Query("SELECT cm FROM ClientMembership cm WHERE cm.expiryDate <= :cutoff AND cm.status = 'ACTIVE'")
//    List<ClientMembership> findExpiringSoon(@Param("cutoff") LocalDate cutoff);
//}
//
//// ─────────────────────────────────────────────────────────────
//// GIFT VOUCHER
//// ─────────────────────────────────────────────────────────────
//@Repository
//interface GiftVoucherRepository extends JpaRepository<GiftVoucher, UUID> {
//    Optional<GiftVoucher> findByCode(String code);
//    Page<GiftVoucher> findByStatus(VoucherStatus status, Pageable pageable);
//    List<GiftVoucher> findByIssuedToId(UUID clientId);
//}
