package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDTO {
    private String chartType;
    private String title;
    private String subtitle;
    private List<String> labels;
    private List<DatasetDTO> datasets;
    private ChartOptionsDTO options;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatasetDTO {
        private String label;
        private List<Number> data;
        private String backgroundColor;
        private String borderColor;
        private Integer borderWidth;
        private Boolean fill;
        private String lineTension; // "smooth" or "straight"
        private List<String> backgroundColors; // For pie/doughnut charts
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartOptionsDTO {
        private Boolean responsive;
        private Boolean maintainAspectRatio;
        private LegendDTO legend;
        private TooltipDTO tooltip;
        private AxesDTO axes;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LegendDTO {
            private Boolean display;
            private String position;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TooltipDTO {
            private Boolean enabled;
            private String mode; // "index", "dataset", "point"
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AxesDTO {
            private AxisDTO xAxis;
            private AxisDTO yAxis;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class AxisDTO {
                private Boolean display;
                private String title;
                private Boolean grid;
            }
        }
    }
}