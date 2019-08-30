import EndpointInput from './EndpointInput';
import NumberInput from './NumberInput';
import TextInput from './TextInput';
import SonarQubeMetricsInput from './SonarQubeMetricsInput';
import LinkListField from './LinkListField';

const dialogFields = {
  EndpointField: {
    component: EndpointInput,
    name: 'endpoint',
    label: 'Endpoint',
    itemsUrl: '/api/endpoints'
  },
  SchedulePeriod: {
    component: NumberInput,
    name: 'schedulePeriod',
    label: 'Schedule Period [seconds]',
    min: 1,
    step: 10,
    initialValue: 120
  },
  Path: {
    component: TextInput,
    name: 'path',
    label: 'Path'
  },
  URL: {
    component: TextInput,
    name: 'url',
    label: 'URL'
  },
  IdString: {
    component: TextInput,
    name: 'idString',
    label: 'ID'
  },
  IdNumber: {
    component: NumberInput,
    name: 'idNumber',
    label: 'ID',
    step: 1
  },
  Key: {
    component: TextInput,
    name: 'keyString',
    label: 'Key'
  },
  SonarQubeMetricsInput: {
    component: SonarQubeMetricsInput,
    name: 'selectedMetrics',
    initialValue: ['blocker_violations', 'critical_violations', 'major_violations', 'minor_violations']
  },
  StatusCode: {
    component: NumberInput,
    name: 'expectedStatusCode',
    label: 'Expected Status Code',
    min: 0,
    step: 1,
    initialValue: 200
  },
  LinkListField: {
    component: LinkListField,
    links:[
      {
        nameProps: {
          name: 'name',
          label: 'Name',
        },
        pathProps: {
          name: 'path',
          label: 'Path',
        }
      }
      ]
  },
};

export default dialogFields;