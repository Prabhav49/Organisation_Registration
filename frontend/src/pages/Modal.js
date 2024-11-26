import React from "react";
import "../css/Modal.css";

const Modal = ({ isOpen, title, message, onClose, onConfirm, isError }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>{title}</h2>
        <p>{message}</p>
        <div className="modal-actions">
          {onConfirm && (
            <button className="modal-confirm-btn" onClick={onConfirm}>
              Yes
            </button>
          )}
          <button className="modal-close-btn" onClick={onClose}>
            {onConfirm ? "No" : "Close"}
          </button>
        </div>
        <div className={`modal-icon ${isError ? "error-icon" : "success-icon"}`}>
          {isError ? "❌" : "✅"}
        </div>
      </div>
    </div>
  );
};

export default Modal;
