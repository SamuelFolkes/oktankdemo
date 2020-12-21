import boto3
import json
from urllib.parse import unquote_plus
from botocore.exceptions import ClientError

client = boto3.client('rekognition')

def lambda_handler(event, context):
    print(json.dumps(event))

    for record in event['Records']:
        bucket = record['s3']['bucket']['name']
        key = unquote_plus(record['s3']['object']['key'])
        identify(bucket,key)


def identify(bucket,key):
    try:
        response = client.detect_labels(
            Image={
                'S3Object': {
                    'Bucket': bucket,
                    'Name': key,
                },
            },
            MaxLabels=10,
            MinConfidence=90,
        )
        print(response)
    except ClientError as e:
        print(e.Message)