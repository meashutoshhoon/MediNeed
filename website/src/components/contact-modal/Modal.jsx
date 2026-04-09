import ModalBackdrop from "./ModalBackdrop";
import SuccessModal from "./SuccessModal";
import ErrorModal from "./ErrorModal";

export default function Modal({ status, closeModal }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center px-4">

      <ModalBackdrop closeModal={closeModal} />

      <div className="relative z-50">
        {status === "success" && <SuccessModal closeModal={closeModal} />}
        {status === "error" && <ErrorModal closeModal={closeModal} />}
      </div>

    </div>
  );
}