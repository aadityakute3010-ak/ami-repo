package com.ami.enums;

public enum ReportExportFormat {

	CSV,
	EXCEL,
	PDF;

	// Defaults to CSV when no format param is supplied; throws for anything unrecognized
	// so we never silently return the wrong file type.
	public static ReportExportFormat fromParam(String value) {

		if (value == null || value.isBlank()) {
			return CSV;
		}

		try {
			return ReportExportFormat.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(
					"Invalid export format: '" + value + "'. Allowed values: csv, excel, pdf");
		}
	}
}