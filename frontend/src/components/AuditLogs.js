import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BASEURL } from '../helper/helper';
import '../css/AuditLogs.css';

const AuditLogs = ({ onClose }) => {
  const [auditLogs, setAuditLogs] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    action: '',
    entityType: '',
    result: '',
    startDate: '',
    endDate: ''
  });

  useEffect(() => {
    fetchAuditLogs();
  }, []);

  const fetchAuditLogs = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem('user');
      const response = await axios.get(`${BASEURL}/api/v1/admin/audit-logs`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      // Ensure response.data is an array
      const logs = Array.isArray(response.data) ? response.data : [];
      setAuditLogs(logs);
    } catch (error) {
      console.error('Failed to fetch audit logs:', error);
      setError('Failed to fetch audit logs. Please try again.');
      setAuditLogs([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    try {
      setFilters({
        ...filters,
        [e.target.name]: e.target.value
      });
    } catch (error) {
      console.error('Error updating filters:', error);
    }
  };

  const filteredLogs = auditLogs.filter(log => {
    try {
      return (
        (!filters.action || (log.action && log.action.toLowerCase().includes(filters.action.toLowerCase()))) &&
        (!filters.entityType || (log.entityType && log.entityType.toLowerCase().includes(filters.entityType.toLowerCase()))) &&
        (!filters.result || (log.status && log.status.toLowerCase().includes(filters.result.toLowerCase()))) &&
        (!filters.startDate || (log.timestamp && new Date(log.timestamp) >= new Date(filters.startDate))) &&
        (!filters.endDate || (log.timestamp && new Date(log.timestamp) <= new Date(filters.endDate)))
      );
    } catch (error) {
      console.error('Error filtering log:', log, error);
      return false;
    }
  });

  const formatTimestamp = (timestamp) => {
    try {
      if (!timestamp) return 'N/A';
      const date = new Date(timestamp);
      if (isNaN(date.getTime())) return 'Invalid Date';
      return date.toLocaleString();
    } catch (error) {
      console.error('Error formatting timestamp:', timestamp, error);
      return 'Error';
    }
  };

  const getResultBadgeClass = (status) => {
    switch (status?.toUpperCase()) {
      case 'SUCCESS': return 'badge-success';
      case 'FAILED': 
      case 'ERROR': return 'badge-failed';
      case 'WARNING': return 'badge-warning';
      default: return 'badge-info';
    }
  };

  return (
    <div className="audit-logs-overlay">
      <div className="audit-logs-container">
        <div className="audit-logs-header">
          <h2>Audit Logs</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        {/* Filters */}
        <div className="audit-filters">
          <div className="filter-row">
            <input
              type="text"
              name="action"
              placeholder="Filter by action..."
              value={filters.action}
              onChange={handleFilterChange}
              className="filter-input"
            />
            <input
              type="text"
              name="entityType"
              placeholder="Filter by entity type..."
              value={filters.entityType}
              onChange={handleFilterChange}
              className="filter-input"
            />
            <select
              name="result"
              value={filters.result}
              onChange={handleFilterChange}
              className="filter-select"
            >
              <option value="">All Status</option>
              <option value="SUCCESS">Success</option>
              <option value="FAILED">Failed</option>
              <option value="ERROR">Error</option>
              <option value="WARNING">Warning</option>
            </select>
          </div>
          <div className="filter-row">
            <input
              type="datetime-local"
              name="startDate"
              value={filters.startDate}
              onChange={handleFilterChange}
              className="filter-input"
            />
            <input
              type="datetime-local"
              name="endDate"
              value={filters.endDate}
              onChange={handleFilterChange}
              className="filter-input"
            />
            <button onClick={fetchAuditLogs} className="refresh-btn">
              Refresh
            </button>
          </div>
        </div>

        {/* Audit Logs Table */}
        <div className="audit-logs-content">
          {error ? (
            <div className="error-message">{error}</div>
          ) : isLoading ? (
            <div className="loading">Loading audit logs...</div>
          ) : (
            <div className="audit-table-container">
              <table className="audit-table">
                <thead>
                  <tr>
                    <th>Timestamp</th>
                    <th>User</th>
                    <th>Action</th>
                    <th>Entity Type</th>
                    <th>Entity ID</th>
                    <th>Status</th>
                    <th>Details</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredLogs.map((log) => (
                    <tr key={log.id || Math.random()}>
                      <td>{log.timestamp ? formatTimestamp(log.timestamp) : 'N/A'}</td>
                      <td>{log.userEmail || 'N/A'}</td>
                      <td>
                        <span className="action-badge">{log.action || 'N/A'}</span>
                      </td>
                      <td>{log.entityType || 'N/A'}</td>
                      <td>{log.entityId || 'N/A'}</td>
                      <td>
                        <span className={`result-badge ${getResultBadgeClass(log.status || 'INFO')}`}>
                          {log.status || 'INFO'}
                        </span>
                      </td>
                      <td>
                        <div className="details" title={log.errorMessage || log.newValues || ''}>
                          {log.errorMessage || log.newValues || 'No details'}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {filteredLogs.length === 0 && !isLoading && !error && (
                <div className="no-logs">No audit logs found</div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AuditLogs;
