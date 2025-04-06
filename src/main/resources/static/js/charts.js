document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("report");
    let salesChart = null;
    let pieChart = null;

    function initChart() {
        if (!salesChart) {
            const salesCtx = document.getElementById("bar-chart").getContext("2d");

            salesChart = new Chart(salesCtx, {
                type: "bar",
                data: {
                    labels: ["Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
                    datasets: [
                        {
                            label: "Income",
                            data: [1200, 1400, 1600, 1800, 2000, 2400], // Replace with actual values
                            backgroundColor: "rgba(46, 204, 113, 0.8)", // Green
                            borderColor: "rgba(46, 204, 113, 1)",
                            borderWidth: 1
                        },
                        {
                            label: "Expense",
                            data: [800, 900, 1000, 1200, 1500, 1600], // Replace with actual values
                            backgroundColor: "rgba(231, 76, 60, 0.8)", // Red
                            borderColor: "rgba(231, 76, 60, 1)",
                            borderWidth: 1
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    indexAxis: 'y', // Horizontal bars
                    plugins: {
                        legend: {
                            position: "bottom", // Moves legend under the bar chart
                            labels: {
                                color: "#A0A0A0", // Gray text for legend
                                font: {
                                    size: 14
                                }
                            }
                        }
                    },
                    scales: {
                        x: {
                            beginAtZero: true,
                            stacked: true
                        },
                        y: {
                            stacked: true
                        }
                    }
                }
            });
        } else {
            salesChart.resize();
        }
    }

    function initPieChart() {
        if (!pieChart) {
            const pieCtx = document.getElementById("pie-chart").getContext("2d");

            pieChart = new Chart(pieCtx, {
                type: "pie",
                data: {
                    labels: ["Direct", "Organic Search", "Referrals"],
                    datasets: [
                        {
                            data: [52.8, 26.8, 20.4], // Replace with actual values
                            backgroundColor: [
                                "#2C6BF6", // Blue for Direct
                                "#20B9BC", // Teal for Organic Search
                                "#9B59B6"  // Purple for Referrals
                            ],
                            borderColor: "#FFFFFF",
                            borderWidth: 2
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: "bottom", // Moves legend under the pie chart
                            labels: {
                                color: "#A0A0A0", // Gray text for legend
                                font: {
                                    size: 14
                                }
                            }
                        },
                        tooltip: {
                            enabled: true,
                            backgroundColor: "#1E1E2D", // Dark tooltip background
                            bodyColor: "#A0A0A0", // Gray text in tooltip
                            titleColor: "#A0A0A0",
                            borderWidth: 1,
                            borderColor: "#FFFFFF"
                        }
                    },
                    animation: {
                        animateScale: false // Disable segment expansion on hover/click
                    }
                }
            });
        } else {
            pieChart.resize();
        }
    }

    const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            if (mutation.attributeName === "class") {
                if (!modal.classList.contains("hidden")) {
                    setTimeout(() => {
                        initChart();
                        initPieChart();
                    }, 10);
                }
            }
        });
    });

    observer.observe(modal, { attributes: true });
});
