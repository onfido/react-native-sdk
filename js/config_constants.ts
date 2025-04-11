export type OnfidoFlowSteps = {
  welcome?: boolean;
  captureDocument?: {
    countryCode?: OnfidoCountryCode;
    alpha2CountryCode?: OnfidoAlpha2CountryCode;
    docType?: OnfidoDocumentType;
    allowedDocumentTypes?: [OnfidoDocumentType]
  };
  captureFace?: OnfidoFaceCapture;
  proofOfAddress?: boolean;
};

export type OnfidoResult = {
  document?: {
    front: {
      id: string;
    };
    back?: {
      id: string;
    };
    nfcMediaId?: {
      id: string;
    };
  };
  face?: {
    id: string;
    variant: OnfidoCaptureType;
  };
  proofOfAddress?:{
    front: {
      id: string;
    };
    back?: {
      id: string;
    };
    type: {
      id: string;
    };
  };
};

export type OnfidoConfig = {
  sdkToken: string;
  hideLogo?: boolean;
  logoCoBrand?: boolean;
   /**
   * @deprecated Deprecated. Use `nfcOption` parameter to manage NFC settings"
   */
  disableNFC?: boolean;
  nfcOption?: OnfidoNFCOptions;
  disableMobileSdkAnalytics?: boolean;
  localisation?: {
    ios_strings_file_name?: string;
  };
  theme: OnfidoTheme;
} & (
  | { workflowRunId: undefined; flowSteps: OnfidoFlowSteps }
  | { workflowRunId: string; flowSteps?: OnfidoFlowSteps }
);

export interface OnfidoError extends Error {
  code?: string;
}

export interface OnfidoMediaResult {
  captureType: "DOCUMENT" | "FACE" | "VIDEO";
}

export interface OnfidoDocumentResult extends OnfidoMediaResult {
  fileData: OnfidoMediaFile;
  documentMetadata: OnfidoDocumentMetadata;
}

export interface OnfidoLivenessResult extends OnfidoMediaResult {
  fileData: OnfidoMediaFile;
}

export interface OnfidoSelfieResult extends OnfidoMediaResult {
  fileData: OnfidoMediaFile;
}

export type OnfidoDocumentMetadata = {
  side: string;
  type: string;
  issuingCountry?: string;
}

export type OnfidoMediaFile = {
  fileData: string;
  fileType: string;
  fileName: string;
}

export enum OnfidoDocumentType {
  PASSPORT = "PASSPORT",
  DRIVING_LICENCE = "DRIVING_LICENCE",
  NATIONAL_IDENTITY_CARD = "NATIONAL_IDENTITY_CARD",
  RESIDENCE_PERMIT = "RESIDENCE_PERMIT",
  VISA = "VISA",
  WORK_PERMIT = "WORK_PERMIT",
  GENERIC = "GENERIC"
}

export enum OnfidoCaptureType {
  PHOTO = "PHOTO",
  VIDEO = "VIDEO",
  MOTION = "MOTION"
}

export enum OnfidoTheme {
  LIGHT = "LIGHT",
  DARK = "DARK",
  AUTOMATIC = "AUTOMATIC"
}

export enum OnfidoNFCOptions {
  DISABLED = "DISABLED",
  OPTIONAL = "OPTIONAL",
  REQUIRED = "REQUIRED"
}

export type OnfidoFaceCapture =
  | OnfidoFaceSelfieCapture
  | OnfidoFaceVideoCapture
  | OnfidoFaceMotionCapture

export type OnfidoFaceSelfieCapture = {
  type: OnfidoCaptureType.PHOTO;
  showIntro?: boolean
};

export type OnfidoFaceVideoCapture = {
  type: OnfidoCaptureType.VIDEO;
  showIntro?: boolean;
  showConfirmation?: boolean;
  manualVideoCapture?: boolean;
};

export type OnfidoFaceMotionCapture = {
    type: OnfidoCaptureType.MOTION;
    recordAudio?: boolean;
};

export enum OnfidoCountryCode {
  ABW = "ABW",
  AFG = "AFG",
  AGO = "AGO",
  AIA = "AIA",
  ALA = "ALA",
  ALB = "ALB",
  AND = "AND",
  ARE = "ARE",
  ARG = "ARG",
  ARM = "ARM",
  ASM = "ASM",
  ATA = "ATA",
  ATF = "ATF",
  ATG = "ATG",
  AUS = "AUS",
  AUT = "AUT",
  AZE = "AZE",
  BDI = "BDI",
  BEL = "BEL",
  BEN = "BEN",
  BES = "BES",
  BFA = "BFA",
  BGD = "BGD",
  BGR = "BGR",
  BHR = "BHR",
  BHS = "BHS",
  BIH = "BIH",
  BLM = "BLM",
  BLR = "BLR",
  BLZ = "BLZ",
  BMU = "BMU",
  BOL = "BOL",
  BRA = "BRA",
  BRB = "BRB",
  BRN = "BRN",
  BTN = "BTN",
  BVT = "BVT",
  BWA = "BWA",
  CAF = "CAF",
  CAN = "CAN",
  CCK = "CCK",
  CHE = "CHE",
  CHL = "CHL",
  CHN = "CHN",
  CIV = "CIV",
  CMR = "CMR",
  COD = "COD",
  COG = "COG",
  COK = "COK",
  COL = "COL",
  COM = "COM",
  CPV = "CPV",
  CRI = "CRI",
  CUB = "CUB",
  CUW = "CUW",
  CXR = "CXR",
  CYM = "CYM",
  CYP = "CYP",
  CZE = "CZE",
  DEU = "DEU",
  DJI = "DJI",
  DMA = "DMA",
  DNK = "DNK",
  DOM = "DOM",
  DZA = "DZA",
  ECU = "ECU",
  EGY = "EGY",
  ERI = "ERI",
  ESH = "ESH",
  ESP = "ESP",
  EST = "EST",
  ETH = "ETH",
  FIN = "FIN",
  FJI = "FJI",
  FLK = "FLK",
  FRA = "FRA",
  FRO = "FRO",
  FSM = "FSM",
  GAB = "GAB",
  GBR = "GBR",
  GEO = "GEO",
  GGY = "GGY",
  GHA = "GHA",
  GIB = "GIB",
  GIN = "GIN",
  GLP = "GLP",
  GMB = "GMB",
  GNB = "GNB",
  GNQ = "GNQ",
  GRC = "GRC",
  GRD = "GRD",
  GRL = "GRL",
  GTM = "GTM",
  GUF = "GUF",
  GUM = "GUM",
  GUY = "GUY",
  HKG = "HKG",
  HMD = "HMD",
  HND = "HND",
  HRV = "HRV",
  HTI = "HTI",
  HUN = "HUN",
  IDN = "IDN",
  IMN = "IMN",
  IND = "IND",
  IOT = "IOT",
  IRL = "IRL",
  IRN = "IRN",
  IRQ = "IRQ",
  ISL = "ISL",
  ISR = "ISR",
  ITA = "ITA",
  JAM = "JAM",
  JEY = "JEY",
  JOR = "JOR",
  JPN = "JPN",
  KAZ = "KAZ",
  KEN = "KEN",
  KGZ = "KGZ",
  KHM = "KHM",
  KIR = "KIR",
  KNA = "KNA",
  KOR = "KOR",
  KWT = "KWT",
  LAO = "LAO",
  LBN = "LBN",
  LBR = "LBR",
  LBY = "LBY",
  LCA = "LCA",
  LIE = "LIE",
  LKA = "LKA",
  LSO = "LSO",
  LTU = "LTU",
  LUX = "LUX",
  LVA = "LVA",
  MAC = "MAC",
  MAF = "MAF",
  MAR = "MAR",
  MCO = "MCO",
  MDA = "MDA",
  MDG = "MDG",
  MDV = "MDV",
  MEX = "MEX",
  MHL = "MHL",
  MKD = "MKD",
  MLI = "MLI",
  MLT = "MLT",
  MMR = "MMR",
  MNE = "MNE",
  MNG = "MNG",
  MNP = "MNP",
  MOZ = "MOZ",
  MSR = "MSR",
  MTQ = "MTQ",
  MUS = "MUS",
  MWI = "MWI",
  MYS = "MYS",
  MYT = "MYT",
  NAM = "NAM",
  NCL = "NCL",
  NER = "NER",
  NFK = "NFK",
  NGA = "NGA",
  NIC = "NIC",
  NIU = "NIU",
  NLD = "NLD",
  NOR = "NOR",
  NPL = "NPL",
  NRU = "NRU",
  NZL = "NZL",
  OMN = "OMN",
  PAK = "PAK",
  PAN = "PAN",
  PCN = "PCN",
  PER = "PER",
  PHL = "PHL",
  PLW = "PLW",
  PNG = "PNG",
  POL = "POL",
  PRI = "PRI",
  PRK = "PRK",
  PRT = "PRT",
  PRY = "PRY",
  PSE = "PSE",
  PYF = "PYF",
  QAT = "QAT",
  REU = "REU",
  RKS = "RKS",
  ROU = "ROU",
  RUS = "RUS",
  RWA = "RWA",
  SAU = "SAU",
  SDN = "SDN",
  SEN = "SEN",
  SGP = "SGP",
  SGS = "SGS",
  SHN = "SHN",
  SJM = "SJM",
  SLB = "SLB",
  SLE = "SLE",
  SLV = "SLV",
  SMR = "SMR",
  SOM = "SOM",
  SPM = "SPM",
  SRB = "SRB",
  SSD = "SSD",
  STP = "STP",
  SUR = "SUR",
  SVK = "SVK",
  SVN = "SVN",
  SWE = "SWE",
  SWZ = "SWZ",
  SXM = "SXM",
  SYC = "SYC",
  SYR = "SYR",
  TCA = "TCA",
  TCD = "TCD",
  TGO = "TGO",
  THA = "THA",
  TJK = "TJK",
  TKL = "TKL",
  TKM = "TKM",
  TLS = "TLS",
  TON = "TON",
  TTO = "TTO",
  TUN = "TUN",
  TUR = "TUR",
  TUV = "TUV",
  TWN = "TWN",
  TZA = "TZA",
  UGA = "UGA",
  UKR = "UKR",
  UMI = "UMI",
  URY = "URY",
  USA = "USA",
  UZB = "UZB",
  VAT = "VAT",
  VCT = "VCT",
  VEN = "VEN",
  VGB = "VGB",
  VIR = "VIR",
  VNM = "VNM",
  VUT = "VUT",
  WLF = "WLF",
  WSM = "WSM",
  XKX = "XKX",
  YEM = "YEM",
  ZAF = "ZAF",
  ZMB = "ZMB",
  ZWE = "ZWE",
}

export enum OnfidoAlpha2CountryCode {
  ABW = "AW",
  AFG = "AF",
  AGO = "AO",
  AIA = "AI",
  ALA = "AX",
  ALB = "AL",
  AND = "AD",
  ARE = "AE",
  ARG = "AR",
  ARM = "AM",
  ASM = "AS",
  ATA = "AQ",
  ATF = "TF",
  ATG = "AG",
  AUS = "AU",
  AUT = "AT",
  AZE = "AZ",
  BDI = "BI",
  BEL = "BE",
  BEN = "BJ",
  BES = "BQ",
  BFA = "BF",
  BGD = "BD",
  BGR = "BG",
  BHR = "BH",
  BHS = "BS",
  BIH = "BA",
  BLM = "BL",
  BLR = "BY",
  BLZ = "BZ",
  BMU = "BM",
  BOL = "BO",
  BRA = "BR",
  BRB = "BB",
  BRN = "BN",
  BTN = "BT",
  BVT = "BV",
  BWA = "BW",
  CAF = "CF",
  CAN = "CA",
  CCK = "CC",
  CHE = "CH",
  CHL = "CL",
  CHN = "CN",
  CIV = "CI",
  CMR = "CM",
  COD = "CD",
  COG = "CG",
  COK = "CK",
  COL = "CO",
  COM = "KM",
  CPV = "CV",
  CRI = "CR",
  CUB = "CU",
  CUW = "CW",
  CXR = "CX",
  CYM = "KY",
  CYP = "CY",
  CZE = "CZ",
  DEU = "DE",
  DJI = "DJ",
  DMA = "DM",
  DNK = "DK",
  DOM = "DO",
  DZA = "DZ",
  ECU = "EC",
  EGY = "EG",
  ERI = "ER",
  ESH = "EH",
  ESP = "ES",
  EST = "EE",
  ETH = "ET",
  FIN = "FI",
  FJI = "FJ",
  FLK = "FK",
  FRA = "FR",
  FRO = "FO",
  FSM = "FM",
  GAB = "GA",
  GBR = "GB",
  GEO = "GE",
  GGY = "GG",
  GHA = "GH",
  GIB = "GI",
  GIN = "GN",
  GLP = "GP",
  GMB = "GM",
  GNB = "GW",
  GNQ = "GQ",
  GRC = "GR",
  GRD = "GD",
  GRL = "GL",
  GTM = "GT",
  GUF = "GF",
  GUM = "GU",
  GUY = "GY",
  HKG = "HK",
  HMD = "HM",
  HND = "HN",
  HRV = "HR",
  HTI = "HT",
  HUN = "HU",
  IDN = "ID",
  IMN = "IM",
  IND = "IN",
  IOT = "IO",
  IRL = "IE",
  IRN = "IR",
  IRQ = "IQ",
  ISL = "IS",
  ISR = "IL",
  ITA = "IT",
  JAM = "JM",
  JEY = "JE",
  JOR = "JO",
  JPN = "JP",
  KAZ = "KZ",
  KEN = "KE",
  KGZ = "KG",
  KHM = "KH",
  KIR = "KI",
  KNA = "KN",
  KOR = "KR",
  KWT = "KW",
  LAO = "LA",
  LBN = "LB",
  LBR = "LR",
  LBY = "LY",
  LCA = "LC",
  LIE = "LI",
  LKA = "LK",
  LSO = "LS",
  LTU = "LT",
  LUX = "LU",
  LVA = "LV",
  MAC = "MO",
  MAF = "MF",
  MAR = "MA",
  MCO = "MC",
  MDA = "MD",
  MDG = "MG",
  MDV = "MV",
  MEX = "MX",
  MHL = "MH",
  MKD = "MK",
  MLI = "ML",
  MLT = "MT",
  MMR = "MM",
  MNE = "ME",
  MNG = "MN",
  MNP = "MP",
  MOZ = "MZ",
  MSR = "MS",
  MTQ = "MQ",
  MUS = "MU",
  MWI = "MW",
  MYS = "MY",
  MYT = "YT",
  NAM = "NA",
  NCL = "NC",
  NER = "NE",
  NFK = "NF",
  NGA = "NG",
  NIC = "NI",
  NIU = "NU",
  NLD = "NL",
  NOR = "NO",
  NPL = "NP",
  NRU = "NR",
  NZL = "NZ",
  OMN = "OM",
  PAK = "PK",
  PAN = "PA",
  PCN = "PN",
  PER = "PE",
  PHL = "PH",
  PLW = "PW",
  PNG = "PG",
  POL = "PL",
  PRI = "PR",
  PRK = "KP",
  PRT = "PT",
  PRY = "PY",
  PSE = "PS",
  PYF = "PF",
  QAT = "QA",
  REU = "RE",
  RKS = "XK",
  ROU = "RO",
  RUS = "RU",
  RWA = "RW",
  SAU = "SA",
  SDN = "SD",
  SEN = "SN",
  SGP = "SG",
  SGS = "GS",
  SHN = "SH",
  SJM = "SJ",
  SLB = "SB",
  SLE = "SL",
  SLV = "SV",
  SMR = "SM",
  SOM = "SO",
  SPM = "PM",
  SRB = "RS",
  SSD = "SS",
  STP = "ST",
  SUR = "SR",
  SVK = "SK",
  SVN = "SI",
  SWE = "SE",
  SWZ = "SZ",
  SXM = "SX",
  SYC = "SC",
  SYR = "SY",
  TCA = "TC",
  TCD = "TD",
  TGO = "TG",
  THA = "TH",
  TJK = "TJ",
  TKL = "TK",
  TKM = "TM",
  TLS = "TL",
  TON = "TO",
  TTO = "TT",
  TUN = "TN",
  TUR = "TR",
  TUV = "TV",
  TWN = "TW",
  TZA = "TZ",
  UGA = "UG",
  UKR = "UA",
  UMI = "UM",
  URY = "UY",
  USA = "US",
  UZB = "UZ",
  VAT = "VA",
  VCT = "VC",
  VEN = "VE",
  VGB = "VG",
  VIR = "VI",
  VNM = "VN",
  VUT = "VU",
  WLF = "WF",
  WSM = "WS",
  XKX = "XK",
  YEM = "YE",
  ZAF = "ZA",
  ZMB = "ZM",
  ZWE = "ZW",
}
