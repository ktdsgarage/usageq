google.charts.load('current', { 'packages': ['corechart'] });
google.charts.setOnLoadCallback(initCharts);

const MAX_DATA_POINTS = 30;

// Chart data stores
let cacheData = [['Time', 'Hit Ratio', 'Miss Ratio']];
let dbData = [['Time', 'Avg Query Time', 'Query Rate', 'Query Errors']];
let responseData = [['Time', 'Avg Response Time', 'Request Rate',  'Response Errors']];

// Chart instances
let cacheChart;
let dbChart;
let responseChart;

function initCharts() {
    cacheChart = new google.visualization.LineChart(document.getElementById('cacheChart'));
    dbChart = new google.visualization.LineChart(document.getElementById('dbChart'));
    responseChart = new google.visualization.LineChart(document.getElementById('responseChart'));

    fetchMetrics();
    setInterval(fetchMetrics, 1000);
}

// Update metrics for specific service
function updateServiceMetrics(service, metrics) {
    // CPU Usage
    const cpuUsage = metrics.find(m => m.name === 'process.cpu.usage')?.value || 0;
    document.getElementById(`${service}CpuUsage`).textContent = (cpuUsage * 100).toFixed(2) + '%';

    // Memory Usage
    const memoryUsage = metrics.find(m => m.name === 'jvm.memory.used')?.value || 0;
    document.getElementById(`${service}MemoryUsage`).textContent = formatBytes(memoryUsage);

    // Heap Usage (Query only)
    if (service === 'query') {
        const heapUsage = metrics.find(m => m.name === 'jvm.gc.memory.allocated')?.value || 0;
        document.getElementById('queryHeapUsage').textContent = formatBytes(heapUsage);
    }

    // Update Query metrics
    if (service === 'query') {
        const timestamp = metrics[0].timestamp;
        const time = formatTimestamp(timestamp);

        // Cache metrics
        const cacheHits = metrics.find(m => m.name === 'usage.cache.hits')?.value || 0;
        const cacheMisses = metrics.find(m => m.name === 'usage.cache.misses')?.value || 0;
        const cacheRequests = cacheHits + cacheMisses;
        const hitRatio = cacheRequests > 0 ? (cacheHits / cacheRequests) : 0;
        const missRatio = cacheRequests > 0 ? (cacheMisses / cacheRequests) : 0;

        document.getElementById('currentCacheHitRatio').textContent = formatPercent(hitRatio);
        document.getElementById('currentCacheMissRatio').textContent = formatPercent(missRatio);

        cacheData.push([time, hitRatio, missRatio]);
        if (cacheData.length > MAX_DATA_POINTS) cacheData.shift();
        drawChart(cacheChart, cacheData, 'Cache Performance');

        // DB metrics
        const queryTime = metrics.find(m => m.name === 'db.query.time')?.value || 0;
        const queryRate = metrics.find(m => m.name === 'db.query.total')?.value || 0;
        const queryErrors = metrics.find(m => m.name === 'db.query.errors')?.value || 0;

        const avgQueryTime = queryRate > 0 ? queryTime / queryRate : 0;

        document.getElementById('currentAvgQueryTime').textContent = avgQueryTime.toFixed(2) + 'ms';
        document.getElementById('currentQueryRate').textContent = queryRate;
        document.getElementById('currentQueryErrors').textContent = queryErrors;

        dbData.push([time, avgQueryTime, queryRate, queryErrors]);
        if (dbData.length > MAX_DATA_POINTS) dbData.shift();
        drawChart(dbChart, dbData, 'Database Performance');

        // Response metrics
        const responseTime = metrics.find(m => m.name === 'usage.response.time')?.value || 0;
        const requestRate = metrics.find(m => m.name === 'usage.requests')?.value || 0;
        const responseErrors = metrics.find(m => m.name === 'usage.errors')?.value || 0;

        const avgResponseTime = requestRate > 0 ? responseTime / requestRate : 0;

        document.getElementById('currentAvgResponseTime').textContent = avgResponseTime.toFixed(2) + 'ms';
        document.getElementById('currentRequestRate').textContent = requestRate;
        document.getElementById('currentResponseErrors').textContent = responseErrors;

        responseData.push([time, avgResponseTime, requestRate,  responseErrors]);
        if (responseData.length > MAX_DATA_POINTS) responseData.shift();
        drawChart(responseChart, responseData, 'Response Performance');
    }

    // Update Management metrics
    if (service === 'management') {
        const updateRequests = metrics.find(m => m.name === 'usage.update.requests')?.value || 0;
        const updateTime = metrics.find(m => m.name === 'usage.update.request.time')?.value || 0;
        const updateErrors = metrics.find(m => m.name === 'usage.update.errors')?.value || 0;

        document.getElementById('currentUpdateRequests').textContent = updateRequests;
        document.getElementById('currentUpdateTime').textContent = updateTime.toFixed(2) + 'ms';
        document.getElementById('currentUpdateErrors').textContent = updateErrors;
    }

    // Update Update metrics
    if (service === 'update') {
        const eventsProcessed = metrics.find(m => m.name === 'events.processed')?.value || 0;
        const eventProcessingTime = metrics.find(m => m.name === 'event.processing.time')?.value || 0;
        const eventErrors = metrics.find(m => m.name === 'events.errors')?.value || 0;
        const fatalEventErrors = metrics.find(m => m.name === 'events.fatal.errors')?.value || 0;

        document.getElementById('currentEventsProcessed').textContent = eventsProcessed;
        document.getElementById('currentEventProcessingTime').textContent = eventProcessingTime.toFixed(2) + 'ms';
        document.getElementById('currentEventErrors').textContent = eventErrors;
        document.getElementById('currentFatalEventErrors').textContent = fatalEventErrors;
    }
}

// Draw chart using Google Charts
function drawChart(chart, data, title) {
    const dataTable = google.visualization.arrayToDataTable(data);
    const options = {
        title: title,
        curveType: 'function',
        legend: { position: 'bottom' },
        hAxis: {
            title: 'Time'
        },
        vAxis: {
            title: 'Value'
        }
    };
    chart.draw(dataTable, options);
}

// Format timestamp for chart labels
function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit', second:'2-digit'});
}

// Format percentage for chart tooltips and labels
function formatPercent(value) {
    return `${(value * 100).toFixed(2)}%`;
}

// Format bytes to KB, MB, GB
function formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
}

// Fetch metrics data for all services
async function fetchMetrics() {
    // Query Service
    try {
        const queryResponse = await fetch(`${window.config.queryService}/api/metrics`);
        const queryData = await queryResponse.json();
        if (queryData.status === 200 && queryData.data) {
            updateServiceMetrics('query', queryData.data);
            document.querySelector('.metric-card').classList.remove('loading', 'error');
        } else {
            document.querySelector('.metric-card').classList.add('error');
            console.error('Error fetching Query Service metrics:', queryData);
        }
    } catch (error) {
        document.querySelector('.metric-card').classList.add('error');
        console.error('Error fetching Query Service metrics:', error);
    }

    // Management Service
    try {
        const managementResponse = await fetch(`${window.config.managementService}/api/metrics`);
        const managementData = await managementResponse.json();
        if (managementData.status === 200 && managementData.data) {
            updateServiceMetrics('management', managementData.data);
            document.querySelectorAll('.service-card')[0].classList.remove('loading', 'error');
        } else {
            document.querySelectorAll('.service-card')[0].classList.add('error');
            console.error('Error fetching Management Service metrics:', managementData);
        }
    } catch (error) {
        document.querySelectorAll('.service-card')[0].classList.add('error');
        console.error('Error fetching Management Service metrics:', error);
    }

    // Update Service
    try {
        const updateResponse = await fetch(`${window.config.updateService}/api/metrics`);
        const updateData = await updateResponse.json();
        if (updateData.status === 200 && updateData.data) {
            updateServiceMetrics('update', updateData.data);
            document.querySelectorAll('.service-card')[1].classList.remove('loading', 'error');
        } else {
            document.querySelectorAll('.service-card')[1].classList.add('error');
            console.error('Error fetching Update Service metrics:', updateData);
        }
    } catch (error) {
        document.querySelectorAll('.service-card')[1].classList.add('error');
        console.error('Error fetching Update Service metrics:', error);
    }
}
