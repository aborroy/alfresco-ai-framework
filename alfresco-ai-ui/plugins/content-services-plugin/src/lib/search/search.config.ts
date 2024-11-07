import { SearchConfiguration } from '@alfresco/adf-content-services';

/**
 * Default Alfresco Content Application configuration for Search
 */
export const DefaultSearchConfiguration: SearchConfiguration = {
  default: true,
  filterWithContains: true,
  // 'aca:fields': ['cm:name', 'cm:title', 'cm:description', 'TEXT', 'TAG'],
  // 'aca:triggeredOnChange': false,
  include: ['path', 'allowableOperations', 'properties'],
  sorting: {
    options: [
      {
        key: 'score',
        label: 'SEARCH.SORT.RELEVANCE',
        type: 'SCORE',
        field: 'score',
        ascending: false
      },
      {
        key: 'name',
        label: 'SEARCH.SORT.FILENAME',
        type: 'FIELD',
        field: 'cm:name',
        ascending: true
      },
      {
        key: 'title',
        label: 'SEARCH.SORT.TITLE',
        type: 'FIELD',
        field: 'cm:title',
        ascending: true
      },
      {
        key: 'modified',
        label: 'SEARCH.SORT.MODIFIED_DATE',
        type: 'FIELD',
        field: 'cm:modified',
        ascending: true
      },
      {
        key: 'modifier',
        label: 'SEARCH.SORT.MODIFIER',
        type: 'FIELD',
        field: 'cm:modifier',
        ascending: true
      },
      {
        key: 'created',
        label: 'SEARCH.SORT.CREATE_DATE',
        type: 'FIELD',
        field: 'cm:created',
        ascending: true
      },
      {
        key: 'content.sizeInBytes',
        label: 'SEARCH.SORT.SIZE',
        type: 'FIELD',
        field: 'content.size',
        ascending: true
      },
      {
        key: 'content.mimetype',
        label: 'SEARCH.SORT.TYPE',
        type: 'FIELD',
        field: 'content.mimetype',
        ascending: true
      }
    ],
    defaults: [
      {
        label: 'Score',
        key: 'score',
        type: 'SCORE',
        field: 'score',
        ascending: false
      }
    ]
  },
  resetButton: true,
  filterQueries: [
    { query: "+TYPE:'cm:folder' OR +TYPE:'cm:content'" },
    {
      query: "-TYPE:'cm:thumbnail' AND -TYPE:'cm:failedThumbnail' AND -TYPE:'cm:rating'"
    },
    { query: '-cm:creator:System' },
    {
      query: "-TYPE:'st:site' AND -ASPECT:'st:siteContainer' AND -ASPECT:'sys:hidden'"
    },
    {
      query: "-TYPE:'dl:dataList' AND -TYPE:'dl:todoList' AND -TYPE:'dl:issue'"
    },
    { query: "-TYPE:'fm:topic' AND -TYPE:'fm:post'" },
    { query: "-TYPE:'lnk:link'" },
    { query: "-PATH:'//cm:wiki/*'" }
  ],
  facetFields: {
    expanded: true,
    fields: [
      {
        mincount: 1,
        field: 'creator',
        label: 'SEARCH.FACET_FIELDS.CREATOR',
        settings: {
          allowUpdateOnChange: false,
          hideDefaultAction: true,
          facetOrder: 200
        }
      },
      {
        mincount: 1,
        field: 'modifier',
        label: 'SEARCH.FACET_FIELDS.MODIFIER',
        settings: {
          allowUpdateOnChange: false,
          hideDefaultAction: true,
          facetOrder: 300
        }
      }
    ]
  },
  categories: [
    {
      id: 'logic',
      name: 'LIB.SEARCH.CATEGORIES.LOGIC',
      enabled: true,
      expanded: false,
      component: {
        selector: 'logical-filter',
        settings: {
          allowUpdateOnChange: false,
          hideDefaultAction: true,
          field: 'cm:name,cm:title,cm:description,TEXT,TAG'
        }
      }
    },
    {
      id: 'properties',
      name: 'LIB.SEARCH.CATEGORIES.PROPERTIES',
      enabled: true,
      expanded: false,
      component: {
        selector: 'properties',
        settings: {
          field: 'content.size,cm:name',
          fileExtensions: [
            '3g2',
            '3gp',
            'acp',
            'aep',
            'ai',
            'aiff',
            'apk',
            'arw',
            'avi',
            'bin',
            'bmp',
            'cgm',
            'class',
            'cr2',
            'css',
            'csv',
            'dita',
            'dng',
            'doc',
            'docm',
            'docx',
            'dotm',
            'dwg',
            'dwt',
            'eps',
            'flac',
            'flv',
            'fm',
            'fodg',
            'gif',
            'gtar',
            'gz',
            'htm',
            'html',
            'icns',
            'ics',
            'ief',
            'indd',
            'jar',
            'java',
            'jp2',
            'jpeg',
            'jpg',
            'js',
            'json',
            'jsp',
            'm4v',
            'man',
            'md',
            'mov',
            'mp3',
            'mp4',
            'mpeg',
            'mpp',
            'mrw',
            'msg',
            'nef',
            'numbers',
            'odb',
            'odf',
            'odg',
            'odi',
            'odm',
            'odp',
            'ods',
            'odt',
            'oga',
            'ogg',
            'ogv',
            'ogx',
            'orf',
            'ott',
            'pages',
            'pbm',
            'pdf',
            'pef',
            'pgm',
            'pmd',
            'png',
            'pnm',
            'pot',
            'potx',
            'ppam',
            'ppj',
            'pps',
            'ppsm',
            'ppt',
            'pptm',
            'pptx',
            'ps',
            'psd',
            'rad',
            'raf',
            'rar',
            'rgb',
            'rss',
            'rtf',
            'rw2',
            'rwl',
            'sda',
            'sdc',
            'sdd',
            'sdp',
            'sds',
            'sdw',
            'sgi',
            'sgl',
            'sgml',
            'sh',
            'sldm',
            'smf',
            'stw',
            'svg',
            'swf',
            'sxi',
            'tar',
            'tex',
            'texi',
            'tif',
            'tiff',
            'ts',
            'tsv',
            'txt',
            'vsd',
            'vsdm',
            'vsdx',
            'vssm',
            'vstm',
            'vstx',
            'wav',
            'webm',
            'wma',
            'wmv',
            'wpd',
            'wrl',
            'x3f',
            'xdp',
            'xhtml',
            'xla',
            'xlam',
            'xls',
            'xlsb',
            'xlsm',
            'xlsx',
            'xltm',
            'xml',
            'xpm',
            'xwd',
            'z',
            'zip'
          ]
        }
      }
    },
    {
      id: 'createdModifiedDateRange',
      name: 'LIB.SEARCH.CATEGORIES.DATE',
      enabled: true,
      expanded: false,
      component: {
        selector: 'date-range',
        settings: {
          field: 'cm:created,cm:modified',
          dateFormat: 'dd-MMM-yy',
          maxDate: 'today',
          displayedLabelsByField: {
            'cm:created': 'LIB.SEARCH.CATEGORIES.DATE_TABS.CREATED',
            'cm:modified': 'LIB.SEARCH.CATEGORIES.DATE_TABS.MODIFIED'
          }
        }
      }
    },
    {
      id: 'location',
      name: 'LIB.SEARCH.FACET_FIELDS.LOCATION',
      enabled: true,
      expanded: false,
      component: {
        selector: 'autocomplete-chips',
        settings: {
          allowUpdateOnChange: false,
          hideDefaultAction: true,
          allowOnlyPredefinedValues: false,
          field: 'SITE',
          autocompleteOptions: [{ value: '_REPOSITORY_', query: "PATH:'/app:company_home//*'" }]
        }
      }
    },
    {
      id: 'tag',
      name: 'Tags',
      enabled: true,
      expanded: false,
      component: {
        selector: 'autocomplete-chips',
        settings: {
          allowUpdateOnChange: false,
          hideDefaultAction: true,
          allowOnlyPredefinedValues: true,
          field: 'TAG'
        }
      },
      rules: {
        visible: 'app.areTagsEnabled'
      }
    },
    {
      id: 'categories',
      name: 'LIB.SEARCH.FACET_FIELDS.CATEGORIES',
      enabled: true,
      expanded: false,
      component: {
        selector: 'autocomplete-chips',
        settings: {
          allowUpdateOnChange: false,
          hideDefaultAction: true,
          allowOnlyPredefinedValues: true,
          field: 'cm:categories'
        }
      },
      rules: {
        visible: 'app.areCategoriesEnabled'
      }
    }
  ],
  highlight: {
    prefix: "<span class='aca-highlight'>",
    postfix: '</span>',
    fields: [
      {
        field: 'cm:title'
      },
      {
        field: 'cm:name'
      },
      {
        field: 'cm:description',
        snippetCount: 1
      },
      {
        field: 'cm:content',
        snippetCount: 1
      }
    ]
  }
};
