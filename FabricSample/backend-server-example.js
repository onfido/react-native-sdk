/**
 * BEGIN BACKEND SERVER CODE EXAMPLE
 *
 * The applicant must be created in your backend server.  The apiToken must never be in code or in
 * memory of the client code, or nefarious actors will be able to mis-use it.
 * If the workflowId is provided, the standard steps will be ovewritten.
 *
 * The code below is meant to demo a working integration of the Onfido React Native SDK.
 */

const createSdkToken = async (applicant, applicationId) => {
  const apiToken = 'YOUR_API_TOKEN_HERE'; // DO NOT expose your api token in client code: keep it on the backend server.
  const workflowId = null; // This is not mandatory for the standard integration, and make sure to keep it on the backend server.

  const applicantResponse = await fetch(
    'https://api.onfido.com/v3/applicants',
    {
      method: 'POST',
      headers: {
        Authorization: 'Token token=' + apiToken,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(applicant),
    },
  );

  if (!applicantResponse.ok) {
    console.log(applicantResponse, 'error');
    return {
      status: 'Unable to  start the SDK',
      message:
        "API Token is required to initiate SDK flow. Check your internet connection or API token. To try again, press 'Launch'",
      sdkToken: null,
    };
  }

  const sdkRequestBody = {
    applicant_id: '',
    application_id: applicationId,
  };

  await applicantResponse
    .json()
    .then(responseJson => (sdkRequestBody.applicant_id = responseJson.id))
    .catch(err => {
      console.log(err, 'error');
      return {
        status: 'Unable to start the SDK',
        message:
          'Unexpected error occurred while trying to get the applicant id from the response.',
        sdkToken: null,
      };
    });

  const sdkTokenResponse = await fetch('https://api.onfido.com/v3/sdk_token', {
    method: 'POST',
    headers: {
      Authorization: 'Token token=' + apiToken,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(sdkRequestBody),
  });

  if (!sdkTokenResponse.ok) {
    console.log(sdkTokenResponse, 'error');
    return {
      status: 'Unable to start the SDK',
      message:
        "Application id is required to initiate SDK flow. Check your internet connection or application id. To try again, press 'Launch'",
      sdkToken: null,
    };
  }

  let sdkToken;
  await sdkTokenResponse
    .json()
    .then(responseJson => (sdkToken = responseJson.token))
    .catch(err => {
      console.log(err, 'error');
      return {
        status: 'Unable to start the SDK',
        message:
          'Unexpected error occurred while trying to get the SDK token from the response.',
        sdkToken: null,
      };
    });

  let workflowRunId;
  if (workflowId) {
    const workflowRunIdBody = {
      workflow_id: workflowId,
      applicant_id: sdkRequestBody.applicant_id,
    };
    const workflowRunIdResponse = await fetch(
      'https://api.onfido.com/v4/workflow_runs',
      {
        method: 'POST',
        headers: {
          Authorization: 'Token token=' + apiToken,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(workflowRunIdBody),
      },
    );

    if (!workflowRunIdResponse.ok) {
      console.log(workflowRunIdResponse, 'error');
      return {
        status: 'Unable to start the SDK',
        message: 'Error retrieving workflow run id from server',
        sdkToken: sdkToken,
        workflowRunId: null,
      };
    }

    await workflowRunIdResponse
      .json()
      .then(responseJson => (workflowRunId = responseJson.id))
      .catch(err => {
        console.log(err, 'error');
        return {
          status: 'Unable to start the SDK',
          message:
            'Unexpected error occurred while trying to get the workflow run id from the response.',
          sdkToken: sdkToken,
          workflowRunId: null,
        };
      });
  }

  return {
    sdkToken,
    workflowRunId,
  };
};

/**
 * END BACKEND SERVER CODE EXAMPLE
 */

export default createSdkToken;
