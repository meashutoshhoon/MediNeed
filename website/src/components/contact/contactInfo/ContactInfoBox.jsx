import ContactInfoBox from "./ContactInfoBox";

import Icon1 from "../../../assets/contact/contact-info-icon1.svg";
import Icon2 from "../../../assets/contact/contact-info-icon2.svg";
import Icon3 from "../../../assets/contact/contact-info-icon3.svg";

export default function ContactInfo() {
  return (
    <div className="grid md:grid-cols-3 gap-6 mt-16">

      <ContactInfoBox
        icon={Icon1}
        textLine1="1211 Awesome Avenue,"
        textLine2="NY USA"
      />

      <ContactInfoBox
        icon={Icon2}
        textLine1="+00 123 - 456 -78"
        textLine2="+00 987 - 654 -32"
      />

      <ContactInfoBox
        icon={Icon3}
        textLine1="mint@mintmail.com"
      />

    </div>
  );
}