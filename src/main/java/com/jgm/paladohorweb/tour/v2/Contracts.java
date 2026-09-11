package com.jgm.paladohorweb.tour.v2;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Contracts {
  public record ExperienceInput(
      @NotBlank @Size(max = 120) String title,
      @NotBlank @Size(max = 5000) String description,
      @Pattern(regexp = "TOUR|STAY|EVENT") @NotNull String kind,
      @NotBlank @Size(max = 100) String department,
      @NotBlank @Size(max = 100) String municipality,
      @NotBlank @Size(max = 255) String address,
      @NotNull @DecimalMin("-90") @DecimalMax("90") Double latitude,
      @NotNull @DecimalMin("-180") @DecimalMax("180") Double longitude,
      @NotNull @DecimalMin("-90") @DecimalMax("90") Double meetingLatitude,
      @NotNull @DecimalMin("-180") @DecimalMax("180") Double meetingLongitude,
      @NotBlank @Size(max = 1500) String meetingInstructions,
      @NotBlank @Pattern(regexp = "57[0-9]{10}") String whatsapp,
      @NotBlank @Size(max = 255) @Pattern(regexp = "https://[^\\s]+") String imageUrl,
      @NotBlank @Size(max = 100) String rnt,
      @NotBlank @Size(max = 3000) String cancellationPolicy,
      @NotNull @DecimalMin("1") @DecimalMax("9999999999") @Digits(integer = 10, fraction = 2)
          BigDecimal price) {}

  public record SlotInput(
      @NotNull @FutureOrPresent LocalDate date,
      @Pattern(regexp = "([01][0-9]|2[0-3]):[0-5][0-9]") @NotNull String time,
      @Min(1) @Max(1000) int capacity) {}

  public record BookingInput(
      @NotNull Long slotId,
      @Min(1) @Max(30) int people,
      LocalDate endDate,
      @NotNull @Pattern(regexp = "[a-zA-Z0-9-]{16,80}") String requestKey,
      @AssertTrue boolean acceptedTerms,
      @AssertTrue boolean acceptedPrivacy,
      @NotNull @Pattern(regexp = "2026-09-v2") String legalVersion) {}

  public record NoticeInput(
      Long userId,
      @NotBlank @Size(max = 120) String title,
      @NotBlank @Size(max = 1500) String message,
      boolean promotional) {}

  public record PrivacyInput(
      @Pattern(regexp = "ACCESS|CORRECT|DELETE|REVOKE|COMPLAINT") @NotNull String kind,
      @NotBlank @Size(max = 2000) String detail) {}
}
