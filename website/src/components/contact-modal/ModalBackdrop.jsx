export default function ModalBackdrop({ closeModal }) {
  return (
    <div
      onClick={closeModal}
      className="fixed inset-0 bg-black/70 backdrop-blur-sm z-40"
    />
  );
}